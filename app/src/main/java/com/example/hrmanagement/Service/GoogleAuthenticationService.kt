package com.example.hrmanagement.Service

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

    lateinit var result: CredentialManager

    suspend fun signIn(context: Context): GoogleAuth {

        try {
            val signInWithGoogleOption = GetSignInWithGoogleOption
                .Builder(serverClientId = context.getString(R.string.default_web_client_id))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            result = CredentialManager.create(context)

            val credResult = result.getCredential(
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
                "Success",
                "www.google.com",
                "Department1"
            )
        }catch (e: Exception){
            return GoogleAuth(
                "","","","","","","Error $e","",""
            )
        }

    }

    suspend fun logout() {
        Firebase.auth.signOut()
        //result.clearCredentialState(ClearCredentialStateRequest())
    }

    fun getCurrentUser() = Firebase.auth.currentUser

}