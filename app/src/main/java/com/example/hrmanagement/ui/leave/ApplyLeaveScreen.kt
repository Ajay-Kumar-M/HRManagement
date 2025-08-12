package com.example.hrmanagement.ui.leave

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
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
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyLeaveScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: ApplyLeaveViewModel
) {
    val leaveTypes = listOf("Casual Leave", "Sick Leave", "On Duty", "Optional Holidays", "Comp Off")
    val leaveReason = viewModel.leaveReason.collectAsStateWithLifecycle()
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val fromDateViewModel = viewModel.fromDate.collectAsStateWithLifecycle()
    val toDateViewModel = viewModel.toDate.collectAsStateWithLifecycle()
    val leaveTypeSelected = viewModel.leaveTypeSelected.collectAsStateWithLifecycle()
    val fromDatePickerState = rememberDatePickerState()
    var fromShowDatePicker by remember { mutableStateOf(false) }
    val fromSelectedDate = fromDateViewModel.value.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
    val toDatePickerState = rememberDatePickerState()
    var toShowDatePicker by remember { mutableStateOf(false) }
    val toSelectedDate = toDateViewModel.value.let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
    } ?: ""
    var leaveTypeExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Next Year"
                    )
                }
                Text("Apply Leave",
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
                        .height(55.dp)
                        .fillMaxWidth(),
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
                            val durationDiff = toDateViewModel.value.minus(fromDateViewModel.value)
                            val differenceInDays = durationDiff.milliseconds.inWholeDays.toInt() + 1
                            if ((differenceInDays>0)&&(leaveTypeSelected.value!="Select Leave Type from Dropdown")){
                                viewModel.appUserData.email.let {
                                    viewModel.addAnnualLeaveData(it)
                                }
                            } else {
                                if (differenceInDays <= 0) {
                                    Toast.makeText(context, "End date should not be lesser than start date!", Toast.LENGTH_LONG).show()
                                }
                                else {
                                    Toast.makeText(context, "Please select Leave Type from dropdown!", Toast.LENGTH_LONG).show()
                                }
                            }
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
                    viewModel.appUserData.email,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp)
                )
                Row {
                    Text(
                        "From",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 2.dp, 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "*",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(0.dp, 15.dp, 5.dp, 2.dp),
                        color = Color.Red
                    )
                }
                Row(
                    modifier = Modifier.padding(20.dp,0.dp)
                        .clickable{
                        fromShowDatePicker = !fromShowDatePicker
                    }
                ) {
                    TextField(
                        value = fromSelectedDate,
                        onValueChange = {},
//                        label = { Text("Select Date") },
                        placeholder = { Text("Select Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { fromShowDatePicker = !fromShowDatePicker }) {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp)
                            .clickable{
                                fromShowDatePicker = !fromShowDatePicker
                            },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                    )
                    if(fromShowDatePicker){
                        DatePickerDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.onFromDateSelected(fromDatePickerState.selectedDateMillis)
                                    fromShowDatePicker = !fromShowDatePicker
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    fromShowDatePicker = !fromShowDatePicker
                                }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = fromDatePickerState)
                        }
                    }
                }
                Row {
                    Text(
                        "To",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 2.dp, 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "*",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(0.dp, 15.dp, 5.dp, 2.dp),
                        color = Color.Red
                    )
                }
                Row(
                    modifier = Modifier.padding(20.dp,0.dp)
                        .clickable{
                        toShowDatePicker = !toShowDatePicker
                    }
                ) {
                    TextField(
                        value = toSelectedDate,
                        onValueChange = {},
//                        label = { Text("Select Date") },
                        placeholder = { Text("Select Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { toShowDatePicker = !toShowDatePicker }) {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                            .height(50.dp)
                    )
                    if(toShowDatePicker){
                        DatePickerDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.onToDateSelected(toDatePickerState.selectedDateMillis)
                                    toShowDatePicker = !toShowDatePicker
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    toShowDatePicker = !toShowDatePicker
                                }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = toDatePickerState)
                        }
                    }
                }
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp),
                    fontWeight = FontWeight.Bold
                )
                val durationDiff = toDateViewModel.value.minus(fromDateViewModel.value)
                Text(
                    "${(durationDiff.milliseconds.inWholeDays.toInt()).plus(1)} Day(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp, 2.dp, 5.dp, 15.dp),
                )
                Row {
                    Text(
                        "Leave Type",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 15.dp, 2.dp, 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "*",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(0.dp, 15.dp, 5.dp, 2.dp),
                        color = Color.Red
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp,0.dp)
                ) {
                    Row{
                        TextField(
                            value = leaveTypeSelected.value,
                            onValueChange = {},
//                            label = { Text("Select Leave Type") },
                            placeholder = { Text("Select Leave Type") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { leaveTypeExpanded = !leaveTypeExpanded }) {
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Leave type options")
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                                .clickable {
                                    leaveTypeExpanded = !leaveTypeExpanded
                                }
                        )
                    }
                    DropdownMenu(
                        expanded = leaveTypeExpanded,
                        onDismissRequest = { leaveTypeExpanded = false },
                        scrollState = rememberScrollState(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        leaveTypes.forEach { leaveType ->
                            DropdownMenuItem(
                                text = { Text(leaveType) },
                                onClick = {
                                    viewModel.onLeaveTypeSelected(leaveType)
                                    leaveTypeExpanded = false
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
                Text(
                    "Reason",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 15.dp, 5.dp, 2.dp),
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = leaveReason.value,
                    onValueChange = {
                        viewModel.onLeaveReasonUpdated(it)
                    },
                    label = { Text("Enter value...") },
                    modifier = Modifier.padding(5.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

            }
        }
    }
}