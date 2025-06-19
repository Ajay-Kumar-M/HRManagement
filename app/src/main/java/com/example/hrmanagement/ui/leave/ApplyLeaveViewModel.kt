package com.example.hrmanagement.ui.leave

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.ui.userinfo.getPropertyValue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class ApplyLeaveViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var year: Int = 0
    var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var _fromDate: MutableStateFlow<Long> = MutableStateFlow(0)
    val fromDate = _fromDate.asStateFlow()
    var _toDate: MutableStateFlow<Long> = MutableStateFlow(0)
    val toDate = _toDate.asStateFlow()
    var _leaveTypeSelected: MutableStateFlow<String> = MutableStateFlow("Select Leave Type from Dropdown")
    val leaveTypeSelected = _leaveTypeSelected.asStateFlow()
    var _leaveReason: MutableStateFlow<String> = MutableStateFlow("")
    val leaveReason = _leaveReason.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>(replay = 0)
    val toastEvent = _toastEvent.asSharedFlow()
    val leaveTypeDataClassMap: Map<String, String> = mapOf(
        Pair("Casual Leave","casualLeaveBalance"),
        Pair("Sick Leave","sickLeaveBalance"),
        Pair("On Duty","onDutyLeaveBalance"),
        Pair("Optional Holidays","optionalLeaveBalance"),
        Pair("Comp Off","compOffLeaveBalance"))
    var leaveType: String = checkNotNull(savedStateHandle["leaveType"])
    var personEmailId: String = checkNotNull(savedStateHandle["personEmailId"])
    var lastLeaveId: Int? = null
    private var numberOfFeatchProcess: Int = 0

    init {
        year = Calendar.getInstance().get(Calendar.YEAR)
        val startOfTheDay = Calendar.getInstance()
        startOfTheDay.apply {
            set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
            set(Calendar.MINUTE, 0)       // Set minutes to 0
            set(Calendar.SECOND, 0)       // Set seconds to 0
            set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
        }
        _fromDate.value = startOfTheDay.timeInMillis
        _toDate.value = fromDate.value
        if (leaveType != "All"){
            onLeaveTypeSelected(leaveType)
        }
        fetchLastLeaveId()
    }

    fun fetchLastLeaveId(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.fetchLastLeaveId(personEmailId){ id, status ->
            if (status == "Success") {
                lastLeaveId = id
            } else {

            }
            numberOfFeatchProcess--
            if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0)) {
                toggleIsViewLoading()
            }
        }
    }

    fun addAnnualLeaveDataResponseListener(response: String){
        numberOfFeatchProcess--
        if(response == "Success"){
            Log.d("ApplyLeaveViewModel","addAnnualLeaveDataResponseListener record added $response")
            clearAllUiFields()
            triggerToast("Record Created")
        } else {
            //handle errors
            triggerToast("Error occurred. Try again!")
        }
        if ((isViewLoading.value == true) && (numberOfFeatchProcess == 0))
            toggleIsViewLoading()
    }

    fun addAnnualLeaveData(personEmailId: String) {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = fromDate.value
        year = calendar.get(Calendar.YEAR)
        appDataManager.getFirebaseLeaveTrackerData(year, personEmailId,::processLeaveRequest)
    }

    fun processLeaveRequest(responseLeaveTrackerData: LeaveTrackerData, response: String) {
        val durationDiff = toDate.value.minus(fromDate.value)
        val differenceInDays = durationDiff.milliseconds.inWholeDays.toInt() + 1
        val selectedLeaveTypeDataClass = leaveTypeDataClassMap.getValue(leaveTypeSelected.value)
        val processedLeaveDaysRemaining = (getPropertyValue(
            responseLeaveTrackerData,
            selectedLeaveTypeDataClass
        ).toString().toInt())-differenceInDays
        if ((processedLeaveDaysRemaining>=0)&&(lastLeaveId!=null)){
            val fromSelectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(fromDate.value))?: ""
            val toSelectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(toDate.value))?: ""
            val dateOfRequest = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(Calendar.getInstance().timeInMillis))?: ""
            val employeeId = responseLeaveTrackerData.employeeID
            lastLeaveId = lastLeaveId!! +1
            responseLeaveTrackerData.lastLeaveId = lastLeaveId!!
            when(selectedLeaveTypeDataClass) {
                "casualLeaveBalance" -> { responseLeaveTrackerData.casualLeaveBalance = processedLeaveDaysRemaining }
                "sickLeaveBalance" -> { responseLeaveTrackerData.sickLeaveBalance = processedLeaveDaysRemaining }
                "onDutyLeaveBalance" -> { responseLeaveTrackerData.onDutyLeaveBalance = processedLeaveDaysRemaining }
                "optionalLeaveBalance" -> { responseLeaveTrackerData.optionalLeaveBalance = processedLeaveDaysRemaining }
                "compOffLeaveBalance" -> { responseLeaveTrackerData.compOffLeaveBalance = processedLeaveDaysRemaining }
            }
//            responseLeaveTrackerData.annualLeaveData.put(
//                "${employeeId}_${responseLeaveTrackerData.lastLeaveId}",
//                mapOf(
//                    Pair("Leave ID","${responseLeaveTrackerData.lastLeaveId}"),
//                    Pair("Leave Type",leaveTypeSelected.value),
//                    Pair("Number Of Days","$differenceInDays"),
//                    Pair("Start Date",fromSelectedDate),
//                    Pair("End Date",toSelectedDate),
//                    Pair("Status","Pending"),
//                    Pair("Email",responseLeaveTrackerData.emailId),
//                    Pair("Employee ID","$employeeId"),
//                    Pair("Employee Name",responseLeaveTrackerData.annualLeaveData.values.first().getValue("Employee Name")),
//                    Pair("Team Email Id",responseLeaveTrackerData.annualLeaveData.values.first().getValue("Team Email Id")),
//                    Pair("Date Of Request",dateOfRequest),
//                    Pair("Reason For Leave", leaveReason.value)
//                )
//            )
            val leaveData = LeaveData(
                lastLeaveId!!,
                leaveTypeSelected.value,
                differenceInDays.toFloat(),
                fromDate.value,
                toDate.value,
                "Approved",
                responseLeaveTrackerData.emailId,
                "$employeeId",
                responseLeaveTrackerData.username,
                responseLeaveTrackerData.teamEmailId,
                Calendar.getInstance().timeInMillis,
                leaveReason.value,
                "",0,0,
                0,0,0,0,
                "","",
                year,
                fromSelectedDate,
                toSelectedDate,
                dateOfRequest,
                mapOf()
            )
//            appDataManager.addLeaveLogTemp(leaveData)
            appDataManager.addLeaveTrackerData(responseLeaveTrackerData, year, leaveData, ::addAnnualLeaveDataResponseListener)
        } else {
            triggerToast("You don't have enough Balance Days from ${leaveTypeSelected.value}")
            Log.d("ApplyLeaveViewModel","You don't have enough Balance Days from ${leaveTypeSelected.value}")
            if (isViewLoading.value==true)
                toggleIsViewLoading()
        }
    }

    fun onFromDateSelected(timestamp: Long?){
        _fromDate.value = timestamp ?: 0
    }

    fun onToDateSelected(timestamp: Long?){
        _toDate.value = timestamp ?: 0
    }

    fun onLeaveTypeSelected(leaveType: String){
        _leaveTypeSelected.value = leaveType
    }

    fun onLeaveReasonUpdated(updatedleaveReason: String){
        _leaveReason.value = updatedleaveReason
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun triggerToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    fun clearAllUiFields() {
        onLeaveReasonUpdated("")
        onLeaveTypeSelected("Select Leave Type from Dropdown")
        val currentTime = Calendar.getInstance().timeInMillis
        onFromDateSelected(currentTime)
        onToDateSelected(currentTime)
        _fromDate.value = currentTime
        _toDate.value = currentTime
    }
}


/*
val employeeId = leaveTrackerData.employeeID
        leaveTrackerData.lastLeaveId = leaveTrackerData.lastLeaveId+1
        leaveTrackerData.annualLeaveData.put(
            "${employeeId}_${leaveTrackerData.lastLeaveId}",
                mapOf(
                    Pair("Leave ID","${leaveTrackerData.lastLeaveId}"),
                    Pair("Leave Type","Casual Leave"),
                    Pair("Number Of Days","1"),
                    Pair("Start Date","23-May-2025"),
                    Pair("End Date","23-MAy-2025"),
                    Pair("Status","Pending"),
                    Pair("Email","ajay.kumar0495@gmail.com"),
                    Pair("Employee ID","1001"),
                    Pair("Employee Name","Ajay Kumar M"),
                    Pair("Team Email Id","team@gmail.com"),
                    Pair("Date Of Request","20-May-2025"),
                    Pair("Reason For Leave","Reason")
                )
        )
 */