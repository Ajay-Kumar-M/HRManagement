package com.example.hrmanagement.ui.leave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.ui.userinfo.UserInfoScreenViewModel

@Composable
fun LeaveDetailsScreen(
    modifier: Modifier,
    navController: NavController,
    leaveData: Map<String,String>,
    viewModel: UserInfoScreenViewModel = viewModel()
) {

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
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
                        contentDescription = "Next Year"
                    )
                }
                Text("Leave Details",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Employee ID",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,15.dp,5.dp,2.dp)
            )
            Text("${leaveData.getValue("Employee ID")} - ${leaveData.getValue("Email")} - ${leaveData.getValue("Employee Name")}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,2.dp,5.dp,15.dp)
            )
            Text("Leave Type",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,15.dp,5.dp,2.dp)
            )
            Text(leaveData.getValue("Leave Type"),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,2.dp,5.dp,15.dp)
            )
            Text("Date",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,15.dp,5.dp,2.dp)
            )
            Text("${leaveData.getValue("Start Date")} - ${leaveData.getValue("End Date")}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,2.dp,5.dp,15.dp)
            )
            Text("Total Day(s)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,15.dp,5.dp,2.dp)
            )
            Text("${leaveData.getValue("Number Of Days")} Day(s)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,2.dp,5.dp,15.dp)
            )
            Text("Team Email ID",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,15.dp,5.dp,2.dp)
            )
            Text(leaveData.getValue("Team Email Id"),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,2.dp,5.dp,15.dp)
            )
            Text("Date Of Request",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,15.dp,5.dp,2.dp)
            )
            Text(leaveData.getValue("Date Of Request"),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,2.dp,5.dp,15.dp)
            )
            Text("Reason for leave",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,15.dp,5.dp,2.dp)
            )
            Text(leaveData.getValue("Reason For Leave"),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(20.dp,2.dp,5.dp,15.dp)
            )
        }
    }
}