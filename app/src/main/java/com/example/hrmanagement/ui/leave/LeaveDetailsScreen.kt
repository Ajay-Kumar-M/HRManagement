package com.example.hrmanagement.ui.leave

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.LeaveData
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LeaveDetailsScreen(
    modifier: Modifier,
    navController: NavController,
    leaveData: LeaveData,
    isApproval: Boolean,
    viewModel: LeaveDetailsViewModel = viewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val popBackStackEvent = viewModel.popBackStackEvent
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()

    // Collect event once and trigger popBackStack
    LaunchedEffect(popBackStackEvent, lifecycleOwner) {
        popBackStackEvent.collect {
            navController.popBackStack()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding(),
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
                        contentDescription = "Next Year"
                    )
                }
                Text("Leave Details",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        },
        bottomBar = {
            if (isApproval){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(55.dp)
                        .background(Color.White)
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledIconButton(
                        onClick = {
                            viewModel.rejectLeave(leaveData)
                        },
                        shape = RoundedCornerShape(16.dp), // Apply rounded corners
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFD70040),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .width(80.dp)
                            .height(40.dp)
                    ) {
                        Text("Reject",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold)
                    }
                    FilledIconButton(
                        onClick = {
                            viewModel.approveLeave(leaveData)
                        },
                        shape = RoundedCornerShape(16.dp), // Apply rounded corners
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF097969),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .width(80.dp)
                            .height(40.dp)
                    ) {
                        Text("Approve",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isViewLoading.value) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicatorComposable()
            }
        } else {
            Column(
                modifier = Modifier.padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Employee ID",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                )
                Text(
                    "${leaveData.employeeId} - ${leaveData.emailId} - ${leaveData.employeeName}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
                Text(
                    "Leave Type",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                )
                Text(
                    leaveData.leaveType,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )

                if (leaveData.unit.isEmpty()) {
                    Text(
                        "From Date",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        leaveData.fromDateString,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Text(
                        "To Date",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        leaveData.toDateString,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Text(
                        "Days Taken",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        "${leaveData.numberOfDays} Day(s)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Text(
                        "Team Email ID",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        leaveData.teamMailId,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Text(
                        "Date Of Request",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        leaveData.dateOfRequestString,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                } else {
                    Text(
                        "Worked Date",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        leaveData.fromDateString,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Box(
                        modifier = Modifier.padding(25.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.White, shape = RoundedCornerShape(15.dp))
                                .padding(15.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column {
                                    Text(
                                        "First In",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        leaveData.otherInfo.getValue("firstIn"),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text(
                                        "Last Out",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        leaveData.otherInfo.getValue("lastOut"),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column {
                                    Text(
                                        "Overtime",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        leaveData.otherInfo.getValue("overtime"),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text(
                                        "Total",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        leaveData.otherInfo.getValue("total"),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        "Duration",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        leaveData.duration,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Text(
                        "Worked Time",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        "${String.format(Locale.US, "%02d", leaveData.startTimeHour)}:${
                            String.format(
                                Locale.US,
                                "%02d",
                                leaveData.startTimeMinute
                            )
                        }-${String.format(Locale.US, "%02d", leaveData.endTimeHour)}:${
                            String.format(
                                Locale.US,
                                "%02d",
                                leaveData.endTimeMinute
                            )
                        }",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Text(
                        "Credited",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        "${leaveData.numberOfDays} Day(s)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                    Text(
                        "Expiry",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                    )
                    Text(
                        leaveData.expiry,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                    )
                }

                Text(
                    "Reason",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp)
                )
                Text(
                    leaveData.reasonForLeave,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
            }
        }
    }
}