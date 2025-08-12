package com.example.hrmanagement.ui.main

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {

    val notificationData = viewModel.notificationData.collectAsStateWithLifecycle()
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()

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
//                    colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = Color.White,
//                        contentColor = Color.Black
//                    ),
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
                    "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 5.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            if (isViewLoading.value) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicatorComposable()
                }
            } else {
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                ) {
                    notificationData.value.forEach { notification ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .clickable {

                                }
                        ) {
                            UserProfileImage(notification.reporterProfileImageUrl)
                            Spacer(Modifier.width(15.dp))
                            Column {
                                Text(
                                    notification.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    completeFormatTimestamp(notification.timestamp),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }

                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }

    }
}


fun completeFormatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}