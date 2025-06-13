package com.example.hrmanagement.ui.leave

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.AttendanceData
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.ui.userinfo.getPropertyValue
import com.google.firebase.firestore.QuerySnapshot
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

class ApplyCompOffViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private var _attendanceData: MutableStateFlow<AttendanceData> = MutableStateFlow(AttendanceData())
    val attendanceData = _attendanceData.asStateFlow()
    private var _durationTypeSelected: MutableStateFlow<String> = MutableStateFlow("Full Day")
    val durationTypeSelected = _durationTypeSelected.asStateFlow()
    private var _unitOptionSelected: MutableStateFlow<String> = MutableStateFlow("Days")
    val unitOptionSelected = _unitOptionSelected.asStateFlow()
    private var _isTimeDialogVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isTimeDialogVisible = _isTimeDialogVisible.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFeatchProcess: Int = 0
    var _workDate: MutableStateFlow<Long> = MutableStateFlow(0)
    val workDate = _workDate.asStateFlow()
    var _leaveReason: MutableStateFlow<String> = MutableStateFlow("")
    val leaveReason = _leaveReason.asStateFlow()
    var personEmailId: String = ""
    var year: Int = 0
    var _timeDurationHr: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeDurationHr = _timeDurationHr.asStateFlow()
    var _timeDurationMin: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeDurationMin = _timeDurationMin.asStateFlow()
    var _startTimeData: MutableStateFlow<Pair<Int,Int>> = MutableStateFlow(Pair(0,0))
    val startTimeData = _startTimeData.asStateFlow()
    var _endTimeData: MutableStateFlow<Pair<Int,Int>> = MutableStateFlow(Pair(0,0))
    val endTimeData = _endTimeData.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>(replay = 0)
    val toastEvent = _toastEvent.asSharedFlow()
    var startTimeTimestamp = 0L
    var endTimeTimestamp = 0L

    init {
        year = Calendar.getInstance().get(Calendar.YEAR)
        personEmailId = checkNotNull(savedStateHandle["userEmailId"])
        val startOfTheDay = Calendar.getInstance()
        startOfTheDay.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _workDate.value = startOfTheDay.timeInMillis
        onWorkDateSelected(_workDate.value)
    }

    fun updateAttendanceDetails(attendanceData: QuerySnapshot?,response: String){
        numberOfFeatchProcess--
        if(response == "Success"){
            Log.d("ApplyCompOffViewModel","updateAttendanceDetails called $attendanceData")
            attendanceData?.first()?.let{ attendanceLog ->
                _attendanceData.value = attendanceLog.toObject(AttendanceData::class.java)
            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun addAnnualLeaveDataResponseListener(response: String){
        if(response == "Success"){
            Log.d("ApplyCompOffViewModel","addAnnualLeaveDataResponseListener record added $response")
            clearAllUiFields()
            if (isViewLoading.value==true) {
                toggleIsViewLoading()
            }
            triggerToast("Record Created")
        } else {
            //handle errors
            if (isViewLoading.value==true) {
                toggleIsViewLoading()
            }
            triggerToast("Error occurred. Try again!")
        }
    }

    fun addAnnualLeaveData() {
        toggleIsViewLoading()
        appDataManager.getFirebaseLeaveTrackerData(year, personEmailId,::processLeaveRequest)
    }

    fun processLeaveRequest(responseLeaveTrackerData: LeaveTrackerData, response: String) {
        if (response=="Success"){
            val workSelectedDate = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(workDate.value))?: ""
            val dateOfRequest = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(Calendar.getInstance().timeInMillis))?: ""
            val employeeId = responseLeaveTrackerData.employeeID
            responseLeaveTrackerData.lastLeaveId = responseLeaveTrackerData.lastLeaveId+1
            responseLeaveTrackerData.annualLeaveData.put(
                "${employeeId}_${responseLeaveTrackerData.lastLeaveId}",
                mapOf(
                    Pair("Leave ID","${responseLeaveTrackerData.lastLeaveId}"),
                    Pair("Leave Type","Comp Off"),
                    Pair("Number Of Days","1"),
                    Pair("Start Date",workSelectedDate),
                    Pair("End Date",workSelectedDate),
                    Pair("Status","Pending"),
                    Pair("Email",responseLeaveTrackerData.emailId),
                    Pair("Employee ID","$employeeId"),
                    Pair("Employee Name",responseLeaveTrackerData.annualLeaveData.values.first().getValue("Employee Name")),
                    Pair("Team Email Id",responseLeaveTrackerData.annualLeaveData.values.first().getValue("Team Email Id")),
                    Pair("Date Of Request",dateOfRequest),
                    Pair("Reason For Leave", leaveReason.value),
                    Pair("Duration", durationTypeSelected.value),
                    Pair("Unit", unitOptionSelected.value),
                    Pair("Duration Hour", timeDurationHr.value.toString()),
                    Pair("Duration Minute", timeDurationMin.value.toString()),
                    Pair("Start Time Hour", startTimeData.value.first.toString()),
                    Pair("Start Time Minute", startTimeData.value.second.toString()),
                    Pair("End Time Hour", endTimeData.value.first.toString()),
                    Pair("End Time Minute", endTimeData.value.second.toString()),
                    Pair("Expiry", "31-DEC-$year")
                )
            )
            appDataManager.addLeaveTrackerData(responseLeaveTrackerData, year, ::addAnnualLeaveDataResponseListener)
        } else {
            triggerToast("Error fetching Users Info! Try again")
            Log.d("ApplyCompOffViewModel","Error fetching Users Info! Try again")
            if (isViewLoading.value==true)
                toggleIsViewLoading()
        }
    }

    fun onWorkDateSelected(timestamp: Long?){
        if (timestamp != null && (personEmailId.isNotBlank() == true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            _workDate.value = timestamp
            numberOfFeatchProcess++
            appDataManager.getFirebaseAttendanceData(workDate.value,(workDate.value+86400000),personEmailId,::updateAttendanceDetails)
        }
    }

    fun onDurationTypeSelected(durationType: String){
        _durationTypeSelected.value = durationType
    }

    fun onLeaveReasonUpdated(updatedleaveReason: String){
        _leaveReason.value = updatedleaveReason
    }

    fun unitOptionChanged(updatedOption: String){
        _unitOptionSelected.value = updatedOption
    }

    fun updateTimeDuration(hour: Int,minute: Int){
        _timeDurationHr.value = hour
        _timeDurationMin.value = minute
    }

    fun updateStartTimeDuration(hour: Int,minute: Int){
        _startTimeData.value = Pair(hour,minute)
    }

    fun updateEndTimeDuration(hour: Int,minute: Int){
        _endTimeData.value = Pair(hour,minute)
    }

    fun toggleTimeDialog(){
        _isTimeDialogVisible.value = !_isTimeDialogVisible.value
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
        onWorkDateSelected(Calendar.getInstance().timeInMillis)
        onDurationTypeSelected("Full Day")
        unitOptionChanged("Days")
        updateTimeDuration(0,0)
        updateStartTimeDuration(0,0)
        updateEndTimeDuration(0,0)
    }
}