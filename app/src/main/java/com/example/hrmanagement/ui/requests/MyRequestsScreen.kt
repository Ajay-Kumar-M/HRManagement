package com.example.hrmanagement.ui.requests

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRequestsScreen(
    modifierPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: MyRequestsViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val tabs = listOf("Pending", "Approved", "Rejected")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(1) }
    val isPullDownRefreshing = remember { mutableStateOf(false) }
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = isPullDownRefreshing.value,
        onRefresh = {
            viewModel.fetchLeaveRequestsData()
            viewModel.fetchAttendanceRegularizationData()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        if (isViewLoading.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicatorComposable()
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(modifierPaddingValues)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.Transparent
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedTabIndex = index
                            },
                            text = { Text(title) }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> {
                        PendingListScreen(navController, viewModel.appUserData.email, viewModel)
                    }
                    1 -> {
                        ApprovedListScreen(navController, viewModel.appUserData.email, viewModel)
                    }
                    2 -> {
                        RejectedListScreen(navController, viewModel.appUserData.email, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun PendingListScreen(
    navController: NavController,
    emailId: String,
    viewModel: MyRequestsViewModel
) {
    var leaveRequestData = viewModel.pendingLeaveRequests.collectAsStateWithLifecycle()
    val attendanceRegularizationRequests = viewModel.attendanceRegularizationRequests.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        if ((leaveRequestData.value.isNotEmpty())||(attendanceRegularizationRequests.value.isNotEmpty())) {
            items(leaveRequestData.value) { leaveData ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clickable {
                            val userJson = Json.encodeToString(leaveData)
                            val encodedUserJson =
                                URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (leaveData.unit.isNotEmpty()){
                            Text(
                                "Leave - ${leaveData.leaveType} Request",
                                style = MaterialTheme.typography.titleSmall,
                            )
                        } else {
                            Text(
                                "Leave - ${leaveData.leaveType}",
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            leaveData.dateOfRequestString,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
//                    Spacer(modifier = Modifier.height(10.dp))
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Data Available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        items(attendanceRegularizationRequests.value) { attendanceData ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clickable {
//                            val userJson = Json.encodeToString(leaveData)
//                            val encodedUserJson =
//                                URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
//                            navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Attendance Regularization Request",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "${attendanceData.friendlyDateValue}-${attendanceData.friendlyMonthValue}-${attendanceData.friendlyYearValue}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
//                    Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun ApprovedListScreen(
    navController: NavController,
    emailId: String,
    viewModel: MyRequestsViewModel
) {
    var approvedLeaveRequests = viewModel.approvedLeaveRequests.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (approvedLeaveRequests.value.isNotEmpty()) {
            items(approvedLeaveRequests.value) { leaveData ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clickable {
                            val userJson = Json.encodeToString(leaveData)
                            val encodedUserJson =
                                URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Leave - ${leaveData.leaveType}",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            leaveData.dateOfRequestString,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Data Available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun RejectedListScreen(
    navController: NavController,
    emailId: String,
    viewModel: MyRequestsViewModel
) {
    var rejectedLeaveRequests = viewModel.rejectedLeaveRequests.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (rejectedLeaveRequests.value.isNotEmpty()) {
            items(rejectedLeaveRequests.value) { leaveData ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .clickable {
                            val userJson = Json.encodeToString(leaveData)
                            val encodedUserJson =
                                URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Leave - ${leaveData.leaveType}",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            leaveData.dateOfRequestString,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
//                    Spacer(modifier = Modifier.height(10.dp))
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No Data Available",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}




