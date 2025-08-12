package com.example.hrmanagement.ui.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.ui.theme.Boulder

@Composable
fun SignUpScreen(
    modifier: Modifier,
    navController: NavController
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        IconButton(
            onClick = {  },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = Boulder
            ),
            modifier = Modifier
                .padding(end = 16.dp)
                .size(30.dp)
                .align(Alignment.End)
        ) {
            Icon(painter = painterResource(R.drawable.ic_close), contentDescription = null)
        }

        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        Button(
            onClick = { signInWithGoogle(context, navController, scope) },
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
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }

}