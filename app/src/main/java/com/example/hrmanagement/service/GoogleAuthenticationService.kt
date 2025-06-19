package com.example.hrmanagement.service

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.hrmanagement.R
import com.example.hrmanagement.data.GoogleAuth
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class GoogleAuthenticationService {

    lateinit var credentialManager: CredentialManager

    suspend fun signIn(context: Context): GoogleAuth? {

        try {
            val signInWithGoogleOption = GetSignInWithGoogleOption
                .Builder(serverClientId = context.getString(R.string.default_web_client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            credentialManager = CredentialManager.create(context)

            val credResult = credentialManager.getCredential(
                request = request,
                context = context
            )

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credResult.credential.data)
            val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val authResult = Firebase.auth.signInWithCredential(credential).await()

            val provider = authResult.credential?.provider
            val token = authResult.user?.getIdToken(true)?.await()?.token
            val userName = authResult.user?.displayName
            val email = authResult.user?.email
            val photoUrl = authResult.user?.photoUrl
            val mobileNumber = authResult.user?.phoneNumber

            return GoogleAuth(
                provider.orEmpty(),
                token = token.orEmpty(),
                username = userName.orEmpty(),
                email = email.orEmpty(),
                imageUrl = photoUrl.toString(),
                mobileNumber = mobileNumber.orEmpty(),
                "Active",
                "www.google.com",
                "Department1",
                mapOf(
                    ("username" to "Dummy User 1"),
                    ("imageUrl" to "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"),
                    ("employeeId" to "3456"),
                    ("emailId" to "dummyuser1@gmail.com"),
                    ("designation" to "Employee"),
                )
            )
        }catch (e: Exception){
            return null
        }

    }

    suspend fun logout() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        Firebase.auth.signOut()
    }

    fun getCurrentUser() = Firebase.auth.currentUser

}