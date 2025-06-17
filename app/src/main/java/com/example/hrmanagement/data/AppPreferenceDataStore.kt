package com.example.hrmanagement.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.firebase.ui.auth.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "com.example.hrmanagement.data")

class AppPreferenceDataStore(context: Context) {
    private val appContext = context

    companion object {
        val PROVIDER = stringPreferencesKey("provider")
        val TOKEN = stringPreferencesKey("token")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_IMAGE_URL = stringPreferencesKey("user_image_url")
        val EMAIL = stringPreferencesKey("email")
        val MOBILE_NUMBER = stringPreferencesKey("mobile_number")
        val MOBILE_NO_COUNTRY = stringPreferencesKey("mobile_no_country")
        val GENDER = stringPreferencesKey("gender")
        val DOB = stringPreferencesKey("dob")
        val EMP_ID = stringPreferencesKey("emp_id")
        val PROFILE_URL = stringPreferencesKey("emp_id")
        val DEPARTMENT_NAME = stringPreferencesKey("department_name")
        val STATUS = stringPreferencesKey("status")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("AppPreferencesDatastore", "Error reading token preference.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[TOKEN] }

    val userImageURLFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("AppPreferencesDatastore", "Error reading token preference.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[USER_IMAGE_URL] }

    val emailFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("AppPreferencesDatastore", "Error reading token preference.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences -> preferences[EMAIL] }

    val userPreferencesFlow: Flow<UserLoginData> = context.dataStore.data.map { preferences ->
        UserLoginData(
            preferences[PROVIDER] ?: "",
            preferences[TOKEN] ?: "",
            preferences[USER_NAME] ?: "",
            preferences[EMAIL] ?: "",
            preferences[USER_IMAGE_URL] ?: "",
            preferences[MOBILE_NUMBER] ?: "",
            preferences[STATUS] ?: "",
            preferences[EMP_ID] ?: "",
            preferences[PROFILE_URL] ?: "",
            preferences[DEPARTMENT_NAME] ?: ""
        )
    }

    suspend fun updateToken(token: String?) {
        appContext.dataStore.edit { preferences ->
            if (token == null) {
                preferences.remove(TOKEN)
            } else {
                preferences[TOKEN] = token
            }
        }
    }

    suspend fun updateGoogleAuthDetails(userDetails: GoogleAuth) {
        appContext.dataStore.edit { preferences ->
            if (userDetails.profileUrl.isBlank()) {
                preferences.remove(PROFILE_URL)
            } else {
                preferences[PROFILE_URL] = userDetails.profileUrl.orEmpty()
            }
            if (userDetails.provider.isBlank()) {
                preferences.remove(PROVIDER)
            } else {
                preferences[PROVIDER] = userDetails.provider
            }
            if (userDetails.username.isBlank()) {
                preferences.remove(USER_NAME)
            } else {
                preferences[USER_NAME] = userDetails.username
            }
            if (userDetails.email.isBlank()) {
                preferences.remove(EMAIL)
            } else {
                preferences[EMAIL] = userDetails.email
            }
            if (userDetails.token.isBlank()) {
                preferences.remove(TOKEN)
            } else {
                preferences[TOKEN] = userDetails.token
            }
            if (userDetails.mobileNumber.isBlank()) {
                preferences.remove(MOBILE_NUMBER)
            } else {
                preferences[MOBILE_NUMBER] = userDetails.mobileNumber
            }
            if (userDetails.imageUrl.isBlank()) {
                preferences.remove(USER_IMAGE_URL)
            } else {
                preferences[USER_IMAGE_URL] = userDetails.imageUrl
            }
            if (userDetails.departmentName.isBlank()) {
                preferences.remove(DEPARTMENT_NAME)
            } else {
                preferences[DEPARTMENT_NAME] = userDetails.departmentName
            }
            if (userDetails.status.isBlank()) {
                preferences.remove(STATUS)
            } else {
                preferences[STATUS] = userDetails.status
            }
        }
    }

    suspend fun updateUserDetails(userDetails: UserLoginData) {
        appContext.dataStore.edit { preferences ->
            preferences[USER_NAME] = userDetails.username
            preferences[TOKEN] = userDetails.token
            preferences[EMAIL] = userDetails.email
            preferences[MOBILE_NUMBER] = userDetails.mobileNumber
            preferences[EMP_ID] = userDetails.emp_Id
            preferences[USER_IMAGE_URL] = userDetails.imageUrl
            preferences[PROVIDER] = userDetails.provider
            preferences[STATUS] = userDetails.status
            preferences[PROFILE_URL] = userDetails.profileUrl
            preferences[DEPARTMENT_NAME] = userDetails.departmentName
        }
    }
}

/*
val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.catch { exception ->
  // dataStore.data throws an IOException when an error is encountered when reading data
  if (exception is IOException) {
    Log.e(TAG, "Error reading preferences.", exception)
    emit(emptyPreferences())
  } else {
    throw exception
  }
}.map { preferences ->
  mapUserPreferences(preferences)
}
 */