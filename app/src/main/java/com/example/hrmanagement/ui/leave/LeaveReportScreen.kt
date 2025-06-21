package com.example.hrmanagement.ui.leave

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.ui.userinfo.getPropertyValue

@Composable
fun LeaveReportScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: LeaveReportViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val leaveTypes = listOf("Casual Leave", "Sick Leave", "On Duty", "Optional Holidays", "Comp Off")
    val leaveTypeIcons: Map<String, ImageVector> = mapOf(
        Pair("Casual Leave",ImageVector.vectorResource(id = R.drawable.leave_casual)),
        Pair("Sick Leave",ImageVector.vectorResource(id = R.drawable.plus_green)),
        Pair("On Duty",ImageVector.vectorResource(id = R.drawable.icons8_person)),
        Pair("Optional Holidays",ImageVector.vectorResource(id = R.drawable.umbrella)),
        Pair("Comp Off",ImageVector.vectorResource(id = R.drawable.calendar_comp_off)))
    val leaveTypeDataClassMap: Map<String, String> = mapOf(
        Pair("Casual LeaveBooked","casualLeaveBooked"),
        Pair("Casual LeaveBalance","casualLeaveBalance"),
        Pair("Sick LeaveBooked","sickLeaveBooked"),
        Pair("Sick LeaveBalance","sickLeaveBalance"),
        Pair("On DutyBooked","onDutyLeaveBooked"),
        Pair("On DutyBalance","onDutyLeaveBalance"),
        Pair("Optional HolidaysBooked","optionalLeaveBooked"),
        Pair("Optional HolidaysBalance","optionalLeaveBalance"),
        Pair("Comp OffBooked","compOffLeaveBooked"),
        Pair("Comp OffBalance","compOffLeaveBalance"))

    val leaveTrackerDetails = viewModel.liveLeaveTrackerDetails.collectAsStateWithLifecycle()
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .background(Color.White)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Home",
                        modifier = Modifier.size(25.dp)
                    )
                }
                Text("Leave Report",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("ApplyLeaveScreen/All")
                },
                containerColor = Color(0xFF1976D2)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(15.dp))
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            if (isViewLoading.value){
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .padding(20.dp)
                ) {
                    items(leaveTypes) { leaveType ->
                        Row (
                            modifier = Modifier.padding(10.dp)
                                .clickable{
                                    navController.navigate("ApplyLeaveScreen/${leaveType}")
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = leaveTypeIcons.getValue(leaveType),
                                contentDescription = "Leave type icon",
                                modifier = Modifier.size(35.dp),
                                tint = Color.Unspecified,
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = leaveType,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Taken : ${getPropertyValue(leaveTrackerDetails.value,leaveTypeDataClassMap.getValue("${leaveType}Booked"))} Day(s) | Balance : ${getPropertyValue(leaveTrackerDetails.value,leaveTypeDataClassMap.getValue("${leaveType}Balance"))} Day(s)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }

//    leaveTypes.forEach { leaveType ->
//        Column(
//            modifier = Modifier
//                .background(Color.White)
//                .clip(RoundedCornerShape(16.dp))
//                .padding(16.dp)
//                .clickable {
//                    viewModel.changeShowBottomSheetLeaveType(leaveType)
//                    viewModel.toggleLeaveTrackerShowBottomSheet()
//                }
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = leaveTypeIcons.getValue(leaveType), //Icons.Default.Share,
//                    contentDescription = "Leave type icon",
//                    modifier = Modifier.size(20.dp)
//                )
//                Text(
//                    leaveType,
//                    style = MaterialTheme.typography.titleSmall,
//                    modifier = Modifier.padding(5.dp, 5.dp, 5.dp, 0.dp)
//                )
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//            Row(horizontalArrangement = Arrangement.SpaceBetween) {
//                Column {
//                    Text(
//                        getPropertyValue(
//                            leaveTrackerData,
//                            leaveTypeDataClassMap.getValue("${leaveType}Balance")
//                        ).toString(),
//                        style = MaterialTheme.typography.bodySmall,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text("Balance", style = MaterialTheme.typography.bodySmall)
//                }
//                Spacer(modifier = Modifier.width(20.dp))
//                Column {
//                    Text(
//                        getPropertyValue(
//                            leaveTrackerData,
//                            leaveTypeDataClassMap.getValue("${leaveType}Booked")
//                        ).toString(),
//                        style = MaterialTheme.typography.bodySmall,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text("Booked", style = MaterialTheme.typography.bodySmall)
//                }
//            }
//        }
//        Spacer(modifier = Modifier.width(20.dp))
//        if (leaveTrackerShowBottomSheet.value) {
//            LeaveTrackerShowModalSheet(leaveTrackerData, leaveTypeDataClassMap, viewModel)
//        }
//    }

}