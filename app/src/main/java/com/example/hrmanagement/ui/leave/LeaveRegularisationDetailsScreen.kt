package com.example.hrmanagement.ui.leave

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.component.completeFormatTimestamp
import com.example.hrmanagement.component.dayOfDate
import com.example.hrmanagement.component.monthNumberToShortName
import com.example.hrmanagement.data.AttendanceRegularisationData
import java.util.Locale

@Composable
fun LeaveRegularisationDetailsScreen(
    modifier: Modifier,
    navController: NavController,
    attendanceData: AttendanceRegularisationData,
    isApproval: Boolean,
    viewModel: LeaveRegularisationDetailsViewModel = viewModel()
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
                        contentDescription = "Previous Screen"
                    )
                }
                Text("Attendance Record",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        },
        bottomBar = {
            if (isApproval){
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(55.dp)
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledIconButton(
                        onClick = {
                            viewModel.rejectRegularizationRequest(attendanceData)
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
                            viewModel.approveRegularizationRequest(attendanceData)
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
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                Text(
                    "Employee Mail",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 20.dp, 5.dp, 2.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    attendanceData.emailId,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
                Text(
                    "Date",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 20.dp, 5.dp, 2.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${attendanceData.friendlyDateValue}/${attendanceData.friendlyMonthValue}/${attendanceData.friendlyYearValue}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                attendanceData.let { //attendanceDataValue ->
                    val day = dayOfDate(it.date)
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(5.dp,0.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight()
                                .padding(15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${it.friendlyDateValue}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                monthNumberToShortName(it.friendlyMonthValue),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                day,
                                style = MaterialTheme.typography.bodySmall
                            )
                            VerticalLineWithBalls()
                        }
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(20.dp))
                                .padding(20.dp)
                        ) {
                            if ((day=="Sat")||(day=="Sun")){
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFFFE5B4), RoundedCornerShape(20.dp))
                                        .padding(8.dp)
                                ){
                                    Text(
                                        "Weekend",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column (
                                    modifier = Modifier
                                        .weight(1f)
                                ){
                                    Text(
                                        "Check-in",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        if((completeFormatTimestamp(it.regularisedCheckInTime).isNotBlank())&&(it.regularisedCheckInTime!=0L)) {
                                            completeFormatTimestamp(it.regularisedCheckInTime)
                                        } else {
                                            "--/--"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        "Check-out",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        if((completeFormatTimestamp(it.regularisedCheckOutTime).isNotBlank())&&(it.regularisedCheckOutTime!=0L)) {
                                            completeFormatTimestamp(it.regularisedCheckOutTime)
                                        } else {
                                            "--/--"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(
                                        String.format(Locale.US,"%.2f", it.regularisedTotalHours),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Hr(s)",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            TextField(
                                value = it.regularisedDescription,
                                onValueChange = { },
                                readOnly = true,
//                                label = { Text("Description") },
                                placeholder = { Text("Description") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE5E4E2),RoundedCornerShape(20.dp))
                                    .padding(1.dp),
                                minLines = 2,
                                singleLine = false,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}