package com.example.hrmanagement.ui.more

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.ui.main.UserProfileImage
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application))
) {

    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous screen"
                    )
                }
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(20.dp, 5.dp)
                        .weight(1f)
                )
                IconButton(
                    onClick = {
                        showExitDialog = true
                    },
//                    colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = Color.Unspecified,
//                        contentColor = Color.Red
//                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.power_off),
                        contentDescription = "Logout",
                        tint = Color.Red
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                UserProfileImage(viewModel.userLoginData.imageUrl)
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        viewModel.userLoginData.username,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 5.dp)
                    )
                    Text(
                        viewModel.userLoginData.email,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 5.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(15.dp))
                    .padding(20.dp, 5.dp)
            ) {
                Text(
                    "Organization Name",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 5.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                "Theme",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(20.dp, 25.dp, 0.dp, 5.dp),
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("ThemeChangeScreen")
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Light Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 10.dp, 0.dp, 15.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = "Change theme",
                    tint = Color.Unspecified
                )
            }
            Text(
                "Others",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(20.dp, 30.dp, 0.dp, 5.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                "Sign Out",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(20.dp, 10.dp, 0.dp, 15.dp)
                    .clickable {
                        showExitDialog = true
                    },
                color = Color.Red
            )


            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = { Text("Confirm Logout") },
                    text = { Text("Are you sure you want to go logout of app?") },
                    confirmButton = {
                        TextButton(onClick = {
                            val job = coroutineScope.launch {
                                isLoading = true
                                appPreferenceDataStore.updateToken(null)
                                MyApplication.googleAuthenticationService.logout(context)
                                isLoading = false
                            }
                            job.invokeOnCompletion {
                                showExitDialog = false
                                navController.navigate("FlashScreen") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }) {
                            if (isLoading) {
                                CircularProgressIndicatorComposable()
                            }
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
            Spacer(Modifier.height(20.dp))
        }

    }
}