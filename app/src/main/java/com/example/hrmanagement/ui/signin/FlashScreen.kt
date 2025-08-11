package com.example.hrmanagement.ui.signin

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.UserSignInStatusData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun FlashScreen(modifier: Modifier,navController: NavController) {

//    var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isViewLoading by rememberSaveable { mutableStateOf(false) }

    Column (
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ) {
        Image(
            painter = painterResource(R.drawable.hrmgmtlogo),
            contentDescription = null,
            alignment = Alignment.Center,
            modifier = Modifier.clip(RoundedCornerShape(30.dp))
        )

        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        if (isViewLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicatorComposable()
            }
        } else {
            Button(
                modifier = Modifier.padding(0.dp,25.dp,0.dp,0.dp),
                onClick = {
                    isViewLoading = true
//                    signInWithGoogle(context, navController, scope)
                    scope.launch {
                        val result = MyApplication.googleAuthenticationService.signIn(context)
                        if((result!=null)&&(result.token.isNotEmpty())){ //&&(result.status=="Active")) {
                            val application = context.applicationContext as MyApplication
//                            Log.d("FlashScreen","${result.token} user token")
//            MyApplication.appPreferenceDataStore.updateToken(result.token)
                            MyApplication.appPreferenceDataStore.updateGoogleAuthDetails(result)
                            application.secureTokenManager?.storeToken(result.token)
                            MyApplication.appDataManager.addGoogleAuthUserData(result)
                            MyApplication.appDataManager.addDummyUserSignInStatus(
                                result.email,
                                UserSignInStatusData(
                                    "Checked-Out",
                                    0,
                                    0
                                ))
                            Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                            isViewLoading = false
                            navController.navigate("MainScreen") {
                                popUpTo("FlashScreen") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Error while authenticating user. Try again!", Toast.LENGTH_LONG).show()
                            isViewLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Image(
                    painter = painterResource(R.drawable.google_login),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = "Sign in with Google",
                    color = Color.Black
                )


            }
        }
    }

}

fun signInWithGoogle(
    context: Context,
    navController: NavController,
    scope: CoroutineScope
) {
    scope.launch {
        val result = MyApplication.googleAuthenticationService.signIn(context)
//        val response = apiService.signIn(result.toSignInRequest())
//        if (response.data == null) return@launch

        if((result!=null)&&(result.token.isNotEmpty())&&(result.status=="Success")) {
            Log.d("FlashScreen","${result.token} user token")
//            MyApplication.appPreferenceDataStore.updateToken(result.token)
            MyApplication.appPreferenceDataStore.updateGoogleAuthDetails(result)
            MyApplication.appDataManager.addGoogleAuthUserData(result)
            Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
            navController.navigate("MainScreen") {
                popUpTo("FlashScreen") { inclusive = true }
            }
        } else {
            Toast.makeText(context, "Error while authenticating user. Try again!", Toast.LENGTH_LONG).show()
        }

//        if (response.data.existingUser) {
//            navController.navigate("MainScreen") {
//                popUpTo("onboarding") { inclusive = true }
//            }
//        } else {
//            navController.navigate("UserDetailScreen")
//        }
    }
}

/*
        ElevatedButton(
            onClick = {  },
            modifier = Modifier.padding(0.dp,25.dp,0.dp,0.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White
            )
        ) {
            Text("Sign In")
        }
        ElevatedButton(
            onClick = {
                navController.navigate("SignUpScreen")
            },
            modifier = Modifier.padding(0.dp,10.dp,0.dp,0.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White
            )
        ) {
            Text("Sign Up")
        }
 */