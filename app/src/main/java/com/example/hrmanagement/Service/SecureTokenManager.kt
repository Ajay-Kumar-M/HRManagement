package com.example.hrmanagement.Service

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys // Use MasterKeys for stable version
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.security.keystore.KeyInfo
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory


sealed class SecureStorageResult<T> {
    data class Success<T>(val data: T) : SecureStorageResult<T>()
    data class Error<T>(val exception: Exception, val message: String) : SecureStorageResult<T>()
}

class SecureTokenManager private constructor(private val context: Context) {

    companion object {
        private const val KEY_ALIAS = "secure_token_key"
        private const val ENCRYPTED_PREFS_NAME = "secure_token_prefs"
        private const val TOKEN_KEY = "encrypted_token"
        private const val IV_KEY = "encryption_iv"
        private const val TAG = "SecureTokenManager"

        @Volatile
        private var INSTANCE: SecureTokenManager? = null

        fun getInstance(context: Context): SecureTokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecureTokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private var encryptedSharedPreferences: SharedPreferences? = null

    init {
        initializeSecureStorage()
    }

    private fun initializeSecureStorage(): SecureStorageResult<Unit> {
        return try {
            // Generate or retrieve the AndroidKeyStore key
            generateOrGetKeystoreKey()

            // Initialize EncryptedSharedPreferences with MasterKeys (stable version)
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                ENCRYPTED_PREFS_NAME,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            Log.d(TAG, "Secure storage initialized successfully")
            SecureStorageResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize secure storage", e)
            SecureStorageResult.Error(e, "Failed to initialize secure storage: ${e.message}")
        }
    }

    private fun generateOrGetKeystoreKey(): SecureStorageResult<SecretKey> {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            // Check if key already exists
            if (keyStore.containsAlias(KEY_ALIAS)) {
                val existingKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
                Log.d(TAG, "Retrieved existing key from keystore")
                return SecureStorageResult.Success(existingKey)
            }

            // Generate new key
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            val secretKey = keyGenerator.generateKey()

            Log.d(TAG, "Generated new key in Android Keystore")
            SecureStorageResult.Success(secretKey)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate or retrieve keystore key", e)
            SecureStorageResult.Error(e, "Keystore key generation failed: ${e.message}")
        }
    }

    fun storeToken(token: String): SecureStorageResult<Unit> {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val encryptedToken = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv

            val prefs = encryptedSharedPreferences ?: return SecureStorageResult.Error(
                IllegalStateException("EncryptedSharedPreferences not initialized"),
                "Storage not initialized"
            )

            val success = prefs.edit()
                .putString(TOKEN_KEY, Base64.encodeToString(encryptedToken, Base64.NO_WRAP))
                .putString(IV_KEY, Base64.encodeToString(iv, Base64.NO_WRAP))
                .commit()

            if (success) {
                Log.d(TAG, "Token stored successfully")
                SecureStorageResult.Success(Unit)
            } else {
                SecureStorageResult.Error(
                    RuntimeException("Failed to commit to SharedPreferences"),
                    "Failed to save encrypted token"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store token", e)
            SecureStorageResult.Error(e, "Token encryption failed: ${e.message}")
        }
    }

    fun retrieveToken(): SecureStorageResult<String> {
        return try {
            val prefs = encryptedSharedPreferences ?: return SecureStorageResult.Error(
                IllegalStateException("EncryptedSharedPreferences not initialized"),
                "Storage not initialized"
            )

            val encryptedTokenString = prefs.getString(TOKEN_KEY, null)
            val ivString = prefs.getString(IV_KEY, null)

            if (encryptedTokenString == null || ivString == null) {
                return SecureStorageResult.Error(
                    NoSuchElementException("Token not found"),
                    "No token found in secure storage"
                )
            }

            val encryptedToken = Base64.decode(encryptedTokenString, Base64.NO_WRAP)
            val iv = Base64.decode(ivString, Base64.NO_WRAP)

            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

            val decryptedToken = cipher.doFinal(encryptedToken)
            val token = String(decryptedToken, Charsets.UTF_8)

            Log.d(TAG, "Token retrieved successfully")
            SecureStorageResult.Success(token)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve token", e)
            SecureStorageResult.Error(e, "Token decryption failed: ${e.message}")
        }
    }

    fun deleteToken(): SecureStorageResult<Unit> {
        return try {
            val prefs = encryptedSharedPreferences ?: return SecureStorageResult.Error(
                IllegalStateException("EncryptedSharedPreferences not initialized"),
                "Storage not initialized"
            )

            val success = prefs.edit()
                .remove(TOKEN_KEY)
                .remove(IV_KEY)
                .commit()

            if (success) {
                Log.d(TAG, "Token deleted successfully")
                SecureStorageResult.Success(Unit)
            } else {
                SecureStorageResult.Error(
                    RuntimeException("Failed to delete from SharedPreferences"),
                    "Failed to delete token"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete token", e)
            SecureStorageResult.Error(e, "Token deletion failed: ${e.message}")
        }
    }

    fun hasToken(): Boolean {
        return try {
            val prefs = encryptedSharedPreferences ?: return false
            prefs.contains(TOKEN_KEY) && prefs.contains(IV_KEY)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check token existence", e)
            false
        }
    }

    fun clearAllSecureData(): SecureStorageResult<Unit> {
        return try {
            val prefs = encryptedSharedPreferences ?: return SecureStorageResult.Error(
                IllegalStateException("EncryptedSharedPreferences not initialized"),
                "Storage not initialized"
            )

            val success = prefs.edit().clear().commit()

            if (success) {
                Log.d(TAG, "All secure data cleared successfully")
                SecureStorageResult.Success(Unit)
            } else {
                SecureStorageResult.Error(
                    RuntimeException("Failed to clear SharedPreferences"),
                    "Failed to clear secure data"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear secure data", e)
            SecureStorageResult.Error(e, "Clear operation failed: ${e.message}")
        }
    }


    fun validateSecurityFeatures(): SecurityValidationResult {
        val results = mutableListOf<String>()

        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            if (keyStore.containsAlias(KEY_ALIAS)) {
                results.add("✓ AndroidKeyStore key exists")

                // Check if key is hardware-backed with proper API level handling
                try {
                    val key = keyStore.getKey(KEY_ALIAS, null) as SecretKey
                    val keyFactory = SecretKeyFactory.getInstance(key.algorithm, "AndroidKeyStore")
                    val keyInfo = keyFactory.getKeySpec(key, KeyInfo::class.java) as KeyInfo

                    // Handle deprecated method properly
                    when {
                        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
                            // API 31+ - Use getSecurityLevel() instead of isInsideSecureHardware()
                            val securityLevel = keyInfo.getSecurityLevel()
                            when (securityLevel) {
                                KeyProperties.SECURITY_LEVEL_SOFTWARE -> {
                                    results.add("⚠ Key is software-only")
                                }
                                KeyProperties.SECURITY_LEVEL_TRUSTED_ENVIRONMENT -> {
                                    results.add("✓ Key is in Trusted Execution Environment (TEE)")
                                }
                                KeyProperties.SECURITY_LEVEL_STRONGBOX -> {
                                    results.add("✓ Key is in StrongBox Secure Element")
                                }
                                else -> {
                                    results.add("⚠ Unknown security level: $securityLevel")
                                }
                            }
                        }
                        else -> {
                            // API 23-30 - Use deprecated isInsideSecureHardware()
                            @Suppress("DEPRECATION")
                            if (keyInfo.isInsideSecureHardware) {
                                results.add("✓ Key is hardware-backed")
                            } else {
                                results.add("⚠ Key is software-only")
                            }
                        }
                    }
                } catch (e: InvalidKeySpecException) {
                    results.add("⚠ Could not determine hardware backing: ${e.message}")
                } catch (e: Exception) {
                    results.add("⚠ Security check failed: ${e.message}")
                }
            } else {
                results.add("⚠ AndroidKeyStore key not found")
            }

            // Check EncryptedSharedPreferences
            if (encryptedSharedPreferences != null) {
                results.add("✓ EncryptedSharedPreferences initialized")
            } else {
                results.add("⚠ EncryptedSharedPreferences not initialized")
            }

        } catch (e: Exception) {
            results.add("⚠ Security validation failed: ${e.message}")
        }

        return SecurityValidationResult(results)
    }

    data class SecurityValidationResult(
        val validationResults: List<String>
    )
}


/*

    fun isKeyHardwareBacked(): Boolean {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                return false
            }

            val key = keyStore.getKey(KEY_ALIAS, null)

            when (key) {
                is SecretKey -> {
                    // For symmetric keys
                    val secretKeyFactory = SecretKeyFactory.getInstance(key.algorithm, "AndroidKeyStore")
                    val keyInfo = secretKeyFactory.getKeySpec(key, KeyInfo::class.java)
                    checkHardwareBacking(keyInfo as KeyInfo)
                }
                is java.security.PrivateKey -> {
                    // For asymmetric keys
                    val keyFactory = java.security.KeyFactory.getInstance(key.algorithm, "AndroidKeyStore")
                    val keyInfo = keyFactory.getKeySpec(key, KeyInfo::class.java)
                    checkHardwareBacking(keyInfo)
                }
                else -> false
            }
        } catch (e: Exception) {
            Log.e("KeyCheck", "Failed to check hardware backing", e)
            false
        }
    }

    private fun checkHardwareBacking(keyInfo: KeyInfo): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // API 31+ - Use getSecurityLevel()
            keyInfo.securityLevel != KeyProperties.SECURITY_LEVEL_SOFTWARE
        } else {
            // API 23-30 - Use deprecated method
            @Suppress("DEPRECATION")
            keyInfo.isInsideSecureHardware
        }
    }
 */