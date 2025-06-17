package com.example.hrmanagement.ui.leave

import android.widget.Toast
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.LeaveTrackerData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyCompOffScreen(
    modifier: Modifier,
    navController: NavController,
    emailId: String,
    viewModel: ApplyCompOffViewModel = viewModel()
) {

    val startTimeData = viewModel.startTimeData.collectAsStateWithLifecycle()
    val endTimeData = viewModel.endTimeData.collectAsStateWithLifecycle()
    val timeDurationHr = viewModel.timeDurationHr.collectAsStateWithLifecycle()
    val timeDurationMin = viewModel.timeDurationMin.collectAsStateWithLifecycle()
    val isTimeDialogVisible = viewModel.isTimeDialogVisible.collectAsStateWithLifecycle()
    val durationTypeSelected = viewModel.durationTypeSelected.collectAsStateWithLifecycle()
    val unitOptionSelected = viewModel.unitOptionSelected.collectAsStateWithLifecycle()
    val attendanceData = viewModel.attendanceData.collectAsStateWithLifecycle()
    val leaveReason = viewModel.leaveReason.collectAsStateWithLifecycle()
    val workDate = viewModel.workDate.collectAsStateWithLifecycle()
    val workDatePickerState = rememberDatePickerState()
    var workShowDatePicker by remember { mutableStateOf(false) }
    val workSelectedDate = workDate.value.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val radioOptions = listOf("Days", "Hours")
    val durationTypes = listOf("Full Day", "Half Day", "Quarter Day")
    var durationTypeExpanded by remember { mutableStateOf(false) }
    val durationTimePickerState = rememberTimePickerState(0,0,true)
    val startimePickerState = rememberTimePickerState(0,0,true)
    var startTimeShowDatePicker by remember { mutableStateOf(false) }
    val endTimePickerState = rememberTimePickerState(0,0,true)
    var endTimeShowDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth(),
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
                        .size(30.dp)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Screen"
                    )
                }
                Text("Add Record",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        },
        bottomBar = {
            if (!isViewLoading.value){
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .background(Color.White)
                        .height(55.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = {
                            navController.popBackStack()
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
                        Text("Cancel",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold)
                    }
                    FilledIconButton(
                        onClick = {
                            viewModel.addAnnualLeaveData()
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
                        Text("Submit",
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
                    emailId,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
                Row {
                    Text(
                        "Worked Date",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "*",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(5.dp, 15.dp, 5.dp, 2.dp),
                        color = Color.Red
                    )
                }
                Row(
                    modifier = Modifier.padding(20.dp,0.dp).clickable{
                        workShowDatePicker = !workShowDatePicker
                    }
                ) {
                    OutlinedTextField(
                        value = workSelectedDate,
                        onValueChange = {},
//                        label = { Text("Select Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { workShowDatePicker = !workShowDatePicker }) {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if(workShowDatePicker){
                        DatePickerDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.onWorkDateSelected(workDatePickerState.selectedDateMillis)
                                    workShowDatePicker = !workShowDatePicker
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    workShowDatePicker = !workShowDatePicker
                                }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = workDatePickerState)
                        }
                    }
                }
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
                                    if (attendanceData.value.checkOutTime==0L) "-" else formatTimestampLegacy(attendanceData.value.checkInTime),
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
                                    if (attendanceData.value.checkOutTime==0L) "-" else formatTimestampLegacy(attendanceData.value.checkOutTime),
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
                                    if (attendanceData.value.checkOutTime==0L) "-" else {
                                        if (isWeekend(attendanceData.value.date)){
                                            "${String.format(Locale.US,"%.2f", attendanceData.value.totalHours)} Hr(s)"
                                        } else {
                                            if(attendanceData.value.totalHours.minus(9.0)>0.0) {
                                                "${String.format(Locale.US,"%.2f", attendanceData.value.totalHours.minus(9.0))} Hr(s)"
                                            } else {
                                                "-"
                                            }
                                        }
                                    },
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
                                    if (attendanceData.value.checkOutTime==0L) "-" else "${String.format(Locale.US,"%.2f", attendanceData.value.totalHours)} Hr(s)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Row (
                    modifier = Modifier.fillMaxWidth()
                            .padding(20.dp, 10.dp, 5.dp, 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "Unit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "*",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }
                Row (
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    radioOptions.forEach { option ->
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (option == unitOptionSelected.value),
                                onClick = { viewModel.unitOptionChanged(option) }
                            )
                            Text(
                                text = option,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.padding(20.dp, 0.dp, 5.dp, 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Duration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "*",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }
                if (unitOptionSelected.value == "Days")
                {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 2.dp, 20.dp, 15.dp),
                    ) {
                        Row{
                            OutlinedTextField(
                                value = durationTypeSelected.value,
                                onValueChange = {},
                                label = { Text("Select Leave Type") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { durationTypeExpanded = !durationTypeExpanded }) {
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Leave type options")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        durationTypeExpanded = !durationTypeExpanded
                                    }
                            )
                        }
                        DropdownMenu(
                            expanded = durationTypeExpanded,
                            onDismissRequest = { durationTypeExpanded = false },
                            scrollState = rememberScrollState(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            durationTypes.forEach { durationType ->
                                DropdownMenuItem(
                                    text = { Text(durationType) },
                                    onClick = {
                                        viewModel.onDurationTypeSelected(durationType)
                                        durationTypeExpanded = false
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                } else {
                    Row (
                        modifier = Modifier.fillMaxWidth()
                            .padding(20.dp, 2.dp, 5.dp, 15.dp)
                            .clickable{
                                viewModel.toggleTimeDialog()
                            }
                    ){
                        Text(
                            "${timeDurationHr.value}:${timeDurationMin.value} hr(s)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (isTimeDialogVisible.value) {
                    AlertDialog(
                        onDismissRequest = { viewModel.toggleTimeDialog() },
                        dismissButton = {
                            TextButton(onClick = {
                                viewModel.toggleTimeDialog()
                            }) {
                                Text("Dismiss")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.updateTimeDuration(durationTimePickerState.hour,durationTimePickerState.minute)
                                viewModel.toggleTimeDialog()
                            }) {
                                Text("OK")
                            }
                        },
                        text = {
                            TimePicker(
                                state = durationTimePickerState,
                            )
                        }
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clickable{
                            startTimeShowDatePicker = true
                        }
                ) {
                    Text(
                        "Start Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                    )
                    Text(
                        "${startTimeData.value.first}:${startTimeData.value.second}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp),
                    )
                }
                if (startTimeShowDatePicker) {
                    AlertDialog(
                        onDismissRequest = { startTimeShowDatePicker = false },
                        dismissButton = {
                            TextButton(onClick = {
                                startTimeShowDatePicker = false
                            }) {
                                Text("Dismiss")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.updateStartTimeDuration(startimePickerState.hour,startimePickerState.minute)
                                startTimeShowDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        text = {
                            TimePicker(
                                state = startimePickerState,
                            )
                        }
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clickable{
                            endTimeShowDatePicker = true
                        }
                ){
                    Text(
                        "End Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                    )
                    Text(
                        "${endTimeData.value.first}:${endTimeData.value.second}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp),
                    )
                }
                if (endTimeShowDatePicker) {
                    AlertDialog(
                        onDismissRequest = { endTimeShowDatePicker = false },
                        dismissButton = {
                            TextButton(onClick = {
                                endTimeShowDatePicker = false
                            }) {
                                Text("Dismiss")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val startTimeTotalMinutes = startimePickerState.hour * 60 + startimePickerState.minute
                                val endTimeTotalMinutes = endTimePickerState.hour * 60 + endTimePickerState.minute
                                if (startTimeTotalMinutes < endTimeTotalMinutes){
                                    viewModel.updateEndTimeDuration(endTimePickerState.hour,endTimePickerState.minute)
                                } else {
                                    viewModel.updateEndTimeDuration(startimePickerState.hour,startimePickerState.minute)
                                }
                                endTimeShowDatePicker = false
                            }) {
                                Text("OK")
                            }
                        },
                        text = {
                            TimePicker(
                                state = endTimePickerState,
                            )
                        }
                    )
                }

                Text(
                    "Expiry Date",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                )
                Text(
                    "31-Dec-${viewModel.year}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp),
                )
                Text(
                    "Reason",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(20.dp, 20.dp, 5.dp, 2.dp),
                )
                Row(
                    modifier = Modifier.padding(20.dp,0.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = leaveReason.value,
                        onValueChange = {
                            viewModel.onLeaveReasonUpdated(it)
                        },
                        label = { Text("Reason for leave") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

            }
        }
    }
}

fun formatTimestampLegacy(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun isWeekend(timestamp: Long): Boolean {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
}