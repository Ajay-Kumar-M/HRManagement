package com.example.hrmanagement.ui.leave

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
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
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.AttendanceRegularisationData
import com.example.hrmanagement.data.UserLoginData
import kotlinx.datetime.Month
import java.nio.file.WatchEvent
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRegularisationScreen(
    modifier: Modifier,
    navController: NavController,
    userEmailId: String,
    username: String?,
    userEmployeeId: String?,
    viewModel: LeaveRegularisationViewModel = viewModel()
) {

    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val periodStartDate = viewModel.periodStartDate.collectAsStateWithLifecycle()
    val periodEndDate = viewModel.periodEndDate.collectAsStateWithLifecycle()
    val periodTypeSelected = viewModel.periodTypeSelected.collectAsStateWithLifecycle()
    val attendanceData = viewModel.attendanceData.collectAsStateWithLifecycle()
    val periodTypes = listOf("Day", "Week", "Month", "Custom")
//    var showCheckInDateTimePicker by remember { mutableStateOf(false) }
//    var showCheckOutDateTimePicker by remember { mutableStateOf(false) }
    var periodTypeExpanded by remember { mutableStateOf(false) }
    val dayPeriodDateViewModel = viewModel.dayPeriodDate.collectAsStateWithLifecycle()
    val periodStartDateTimestamp = viewModel.periodStartDateTimestamp.collectAsStateWithLifecycle()
    val periodEndDateTimestamp = viewModel.periodEndDateTimestamp.collectAsStateWithLifecycle()
    val dayPeriodDatePickerState = rememberDatePickerState()
    val customPeriodFromDatePickerState = rememberDatePickerState()
    val customPeriodEndDatePickerState = rememberDatePickerState()
    var dayPeriodShowDatePicker by remember { mutableStateOf(false) }
    var customPeriodFromShowDatePicker by remember { mutableStateOf(false) }
    var customPeriodEndShowDatePicker by remember { mutableStateOf(false) }
    val dayPeriodSelectedDate = dayPeriodDateViewModel.value.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
    val customPeriodStartSelectedDate = periodStartDateTimestamp.value.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
    val customPeriodEndSelectedDate = periodEndDateTimestamp.value.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
    val context = LocalContext.current
    var checkInAttendance by remember { mutableStateOf<AttendanceRegularisationData?>(null) }
    var checkOutAttendance by remember { mutableStateOf<AttendanceRegularisationData?>(null) }


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
                        .size(40.dp)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Screen"
                    )
                }
                Text("Add Record",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        },
        bottomBar = {
            if (!isViewLoading.value){
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
                            viewModel.addRegularisationAttendanceDetails(userEmailId)
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
            ) {
                Text(
                    "Employee",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 20.dp, 5.dp, 2.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    username.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
                Text(
                    "Employee ID",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    userEmployeeId.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
                Row(
                    modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Period",
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 2.dp, 20.dp, 15.dp),
                ) {
                    Row{
                        OutlinedTextField(
                            value = periodTypeSelected.value,
                            onValueChange = {},
                            label = { Text("Select Leave Type") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { periodTypeExpanded = !periodTypeExpanded }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "period type options")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    periodTypeExpanded = !periodTypeExpanded
                                }
                        )
                    }
                    DropdownMenu(
                        expanded = periodTypeExpanded,
                        onDismissRequest = { periodTypeExpanded = false },
                        scrollState = rememberScrollState(),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        periodTypes.forEach { periodType ->
                            DropdownMenuItem(
                                text = { Text(periodType) },
                                onClick = {
                                    viewModel.onPeriodTypeSelected(periodType)
                                    periodTypeExpanded = false
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
                when(periodTypeSelected.value){
                    "Day" -> {
                        Row {
                            Text(
                                "Date",
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
                            modifier = Modifier.padding(20.dp,0.dp)
                                .clickable{
                                    dayPeriodShowDatePicker = !dayPeriodShowDatePicker
                                }
                        ) {
                            OutlinedTextField(
                                value = dayPeriodSelectedDate,
                                onValueChange = {},
//                                label = { Text("Select Date") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { dayPeriodShowDatePicker = !dayPeriodShowDatePicker }) {
                                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                                    }
                                },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth().clickable{
                                    dayPeriodShowDatePicker = !dayPeriodShowDatePicker
                                }
                            )
                            if(dayPeriodShowDatePicker){
                                DatePickerDialog(
                                    onDismissRequest = {},
                                    confirmButton = {
                                        TextButton(onClick = {
                                            dayPeriodDatePickerState.selectedDateMillis?.let {
                                                viewModel.onDayPeriodDateSelected(convertToStartOfDayInGMT(it))
                                                dayPeriodShowDatePicker = !dayPeriodShowDatePicker
                                            }
                                        }) {
                                            Text("OK")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = {
                                            dayPeriodShowDatePicker = !dayPeriodShowDatePicker
                                        }) {
                                            Text("Cancel")
                                        }
                                    }
                                ) {
                                    DatePicker(state = dayPeriodDatePickerState)
                                }
                            }
                        }
                    }
                    "Week","Month" -> {
                        Text(
                            "From Date",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            periodStartDate.value,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                        )
                        Text(
                            "End Date",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(20.dp, 10.dp, 5.dp, 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            periodEndDate.value,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                        )
                    }
                    "Custom" -> {
                        Row {
                            Text(
                                "From Date",
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
                            modifier = Modifier.padding(20.dp,0.dp)
                                .clickable{
                                    customPeriodFromShowDatePicker = !customPeriodFromShowDatePicker
                                }
                        ) {
                            OutlinedTextField(
                                value = customPeriodStartSelectedDate,
                                onValueChange = {},
//                                label = { Text("Select Date") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { customPeriodFromShowDatePicker = !customPeriodFromShowDatePicker }) {
                                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                                    }
                                },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth().clickable{
                                    customPeriodFromShowDatePicker = !customPeriodFromShowDatePicker
                                }
                            )
                            if(customPeriodFromShowDatePicker){
                                DatePickerDialog(
                                    onDismissRequest = {},
                                    confirmButton = {
                                        TextButton(onClick = {
                                            customPeriodFromDatePickerState.selectedDateMillis?.let {
                                                val fromTimestamp = convertToStartOfDayInGMT(it)
                                                if((periodEndDateTimestamp.value-fromTimestamp)>2678400000){
                                                    Toast.makeText(context,"Total days cannot exceed 31 Days",Toast.LENGTH_LONG).show()
                                                } else if ((periodEndDateTimestamp.value-fromTimestamp)<0){
                                                    Toast.makeText(context,"End Date cannot be before Start Date",Toast.LENGTH_LONG).show()
                                                } else {
                                                    viewModel.onCustomStartPeriodDateSelected(fromTimestamp)
                                                    customPeriodFromShowDatePicker = !customPeriodFromShowDatePicker
                                                }
                                            }
                                        }) {
                                            Text("OK")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = {
                                            customPeriodFromShowDatePicker = !customPeriodFromShowDatePicker
                                        }) {
                                            Text("Cancel")
                                        }
                                    }
                                ) {
                                    DatePicker(state = customPeriodFromDatePickerState)
                                }
                            }
                        }
                        Row {
                            Text(
                                "End Date",
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
                            modifier = Modifier.padding(20.dp,0.dp)
                                .clickable{
                                    customPeriodEndShowDatePicker = !customPeriodEndShowDatePicker
                                }
                        ) {
                            OutlinedTextField(
                                value = customPeriodEndSelectedDate,
                                onValueChange = {},
//                                label = { Text("Select Date") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { customPeriodEndShowDatePicker = !customPeriodEndShowDatePicker }) {
                                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                                    }
                                },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth().clickable{
                                    customPeriodEndShowDatePicker = !customPeriodEndShowDatePicker
                                }
                            )
                            if(customPeriodEndShowDatePicker){
                                DatePickerDialog(
                                    onDismissRequest = {},
                                    confirmButton = {
                                        TextButton(onClick = {
                                            customPeriodEndDatePickerState.selectedDateMillis?.let {
                                                val endTimestamp = convertToStartOfDayInGMT(it)
                                                if((endTimestamp - periodStartDateTimestamp.value)>2678400000){
                                                    Toast.makeText(context,"Total Days cannot exceed 31 Days",Toast.LENGTH_LONG).show()
                                                } else if ((endTimestamp - periodStartDateTimestamp.value)<0){
                                                    Toast.makeText(context,"End Date cannot be before Start Date",Toast.LENGTH_LONG).show()
                                                } else {
                                                    viewModel.onCustomEndPeriodDateSelected(endTimestamp)
                                                    customPeriodEndShowDatePicker = !customPeriodEndShowDatePicker
                                                }
                                            }
                                        }) {
                                            Text("OK")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = {
                                            customPeriodEndShowDatePicker = !customPeriodEndShowDatePicker
                                        }) {
                                            Text("Cancel")
                                        }
                                    }
                                ) {
                                    DatePicker(state = customPeriodEndDatePickerState)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                attendanceData.value.forEach { //attendanceDataValue ->
                        println("screen $attendanceData")
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
                                        .clickable{
                                            checkInAttendance = it
                                        }
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
                                    modifier = Modifier.clickable{
                                        checkOutAttendance = it
                                    }
                                    .weight(1f)
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
                                onValueChange = { changedValue ->
                                    viewModel.changeRegularisedDescription(changedValue,it.date)
                                },
//                                label = { Text("Description") },
                                placeholder = { Text("Description") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE5E4E2),RoundedCornerShape(20.dp))
                                    .padding(1.dp),
                                minLines = 2,
                                singleLine = false
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Reset",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Blue,
                                    modifier = Modifier.clickable{
                                        viewModel.changeRegularisedDescription("",it.date)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text(
                    "DayOfDate(attendanceData.date)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                checkInAttendance?.let { attendance ->
                    DateTimePickerDialog(
                        attendance.date,
                        attendance.regularisedCheckInTime,
                        {checkInAttendance = null},
                        { dateTime ->
                            if((attendance.regularisedCheckOutTime==0L)) {
                                attendance.regularisedCheckOutTime=dateTime
                            }
                            val diffTimestamp = attendance.regularisedCheckOutTime.minus(dateTime)
                            if (diffTimestamp > 86400000L){
                                Toast.makeText(context,"Total hours cannot exceed 24 hours",Toast.LENGTH_LONG).show()
                            } else if(diffTimestamp<0){
                                Toast.makeText(context,"End time cannot be before Start time",Toast.LENGTH_LONG).show()
                            } else {
                                viewModel.checkInTimestampSelected(dateTime,attendance.date,diffTimestamp)
                                checkInAttendance = null
                            }
                        }
                    )
                }

                checkOutAttendance?.let { attendance ->
                    DateTimePickerDialog(
                        attendance.date,
                        attendance.regularisedCheckOutTime,
                        {checkOutAttendance = null},
                        { dateTime ->
                            if((attendance.regularisedCheckInTime==0L)) {
                                attendance.regularisedCheckInTime=dateTime
                            }
                            val diffTimestamp = dateTime.minus(attendance.regularisedCheckInTime)
                            if (diffTimestamp > 86400000L){
                                Toast.makeText(context,"Total hours cannot exceed 24 hours",Toast.LENGTH_LONG).show()
                            } else if(diffTimestamp<0){
                                Toast.makeText(context,"End time cannot be before Start time",Toast.LENGTH_LONG).show()
                            } else{
                                viewModel.checkOutTimestampSelected(dateTime,attendance.date,diffTimestamp)
                                checkOutAttendance = null
                            }
                        }
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    startOfDay: Long,
    timestamp: Long,
    onDismiss: () -> Unit,
    onDateTimeSelected: (Long) -> Unit
) {
    val alertTabs = listOf("DATE", "TIME")
    var selectedTabIndex by remember { mutableIntStateOf(1) }
    val date = Instant.ofEpochMilli(startOfDay).atZone(ZoneOffset.UTC).toLocalDate()
    val midnightMillis = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val nextDayMidnightMillis = date.plusDays(2).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    val yourMillisList = setOf(midnightMillis,nextDayMidnightMillis)
//    val allowedDatesMillis = yourMillisList.map { normalizeToMidnightUtc(it) }.toSet()
    val selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return yourMillisList.contains(utcTimeMillis)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        println("startof the day $startOfDay")
        val initialDateTime = if(timestamp==0L){
            timestampToLocalDateTime(midnightMillis)
        } else{
            timestampToLocalDateTime(timestamp)
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            selectableDates = selectableDates
        )
        val timePickerState = rememberTimePickerState(
            initialHour = initialDateTime.hour,
            initialMinute = initialDateTime.minute,
            is24Hour = true // or true, depending on your UI
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column {
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 3.dp
                ) {
                    alertTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                            },
                            text = { Text(title) }
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (selectedTabIndex) {
                        0 -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            ) {
                                DatePicker(
                                    state = datePickerState,
                                )
                            }
                        }

                        1 -> {
                            TimePicker(
                                state = timePickerState,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(15.dp))
                    TextButton(onClick = {
//                            onDateTimeSelected(LocalDateTime.of(pickedDate, pickedTime))
                        onDateTimeSelected(
                            getDateTimeMills(datePickerState, timePickerState) ?: 0
                        )
                    }) { Text("OK") }
                }
            }
        }
    }
}

//fun normalizeToMidnightUtc(millis: Long): Long {
//    val instant = Instant.ofEpochMilli(millis)
//    val date = instant.atZone(ZoneOffset.UTC).toLocalDate()
//    return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
//}
//
//fun convertGMTToLocalTime(
//    gmtTimestamp: Long,
//    zoneId: ZoneId = ZoneId.systemDefault() // or ZoneId.of("Asia/Kolkata")
//): ZonedDateTime {
//    return Instant.ofEpochMilli(gmtTimestamp)
//        .atZone(ZoneId.of("UTC"))           // Interpret timestamp as GMT
//        .withZoneSameInstant(zoneId)        // Convert to your zone
//}

fun timestampToLocalDateTime(timestamp: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return Instant.ofEpochMilli(timestamp)
        .atZone(zoneId)
        .toLocalDateTime()
}

@OptIn(ExperimentalMaterial3Api::class)
fun getDateTimeMills(
    datePickerState: DatePickerState,
    timePickerState: TimePickerState,
    zoneId: ZoneId = ZoneId.systemDefault()
): Long? = datePickerState.selectedDateMillis?.let { dateMillis ->
    val localDate = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate()
    val localTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
    LocalDateTime.of(localDate, localTime)
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()
}

fun convertToStartOfDayInGMT(selectedDateMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): Long {
    val localDate = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(zoneId)
        .toLocalDate() // Extract just the date (no time)
    return localDate
        .atStartOfDay(zoneId) // Set time to 00:00 in local timezone
        .toInstant()          // Convert to Instant (GMT)
        .toEpochMilli()       // Get millis
}

//fun localDateTimeToMillis(dateTime: LocalDateTime, zoneId: ZoneId = ZoneId.systemDefault()): Long {
//    val zonedDateTime: ZonedDateTime = dateTime.atZone(zoneId)
//    return zonedDateTime.toInstant().toEpochMilli()
//}

fun dayOfDate(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val date = calendar.time
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    return dayFormat.format(date)
}

fun monthNumberToShortName(month: Int): String {
    return Month.of(month)
        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH) // "Jan", "Feb", "Mar", ...
}

fun completeFormatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

fun dateFormatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}

@Composable
fun VerticalLineWithBalls(
    lineColor: Color = Color.Black,
    ballColor: Color = Color.Black,
    lineWidth: Dp = 1.dp,
    ballRadius: Dp = 4.dp
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(ballRadius * 2) // Make space for the ball
            .drawBehind {
                val centerX = size.width / 2
                val topY = 0f
                val bottomY = size.height

                // Draw line
                drawLine(
                    color = lineColor,
                    start = Offset(centerX, topY + ballRadius.toPx()),
                    end = Offset(centerX, bottomY - ballRadius.toPx()),
                    strokeWidth = lineWidth.toPx()
                )

                // Draw top ball
                drawCircle(
                    color = ballColor,
                    radius = ballRadius.toPx(),
                    center = Offset(centerX, topY + ballRadius.toPx())
                )

                // Draw bottom ball
                drawCircle(
                    color = ballColor,
                    radius = ballRadius.toPx(),
                    center = Offset(centerX, bottomY - ballRadius.toPx())
                )
            }
    )
}

/*
pickedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

//                        onDateChange = { millis ->
//                            pickedDate = Instant.ofEpochMilli(millis)
//                                .atZone(ZoneId.systemDefault())
//                                .toLocalDate()
//                        }

//                        onTimeChange = { hour, minute ->
//                            pickedTime = pickedTime.withHour(hour).withMinute(minute)
//                        }

 */