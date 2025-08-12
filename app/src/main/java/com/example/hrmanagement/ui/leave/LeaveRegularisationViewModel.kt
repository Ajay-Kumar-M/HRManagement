package com.example.hrmanagement.ui.leave

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.AttendanceRegularisationData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LeaveRegularisationViewModel(application: Application): AndroidViewModel(application) {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _attendanceData: MutableStateFlow<MutableList<AttendanceRegularisationData>> = MutableStateFlow(mutableListOf())
    val attendanceData = _attendanceData.asStateFlow()
    private var _periodTypeSelected: MutableStateFlow<String> = MutableStateFlow("Day")
    val periodTypeSelected = _periodTypeSelected.asStateFlow()
    private var _dayPeriodDate: MutableStateFlow<Long> = MutableStateFlow(0L)
    val dayPeriodDate = _dayPeriodDate.asStateFlow()
    private var _periodStartDateTimestamp: MutableStateFlow<Long> = MutableStateFlow(0L)
    val periodStartDateTimestamp = _periodStartDateTimestamp.asStateFlow()
    private var _periodEndDateTimestamp: MutableStateFlow<Long> = MutableStateFlow(0L)
    val periodEndDateTimestamp = _periodEndDateTimestamp.asStateFlow()
    private var numberOfFetchProcess: Int = 0
//    private var periodStartDateTimestamp: Long = 0
//    private var periodEndDateTimestamp: Long = 0
    private val _periodStartDate: MutableStateFlow<String> = MutableStateFlow("")
    val periodStartDate = _periodStartDate.asStateFlow()
    private val _periodEndDate: MutableStateFlow<String> = MutableStateFlow("")
    val periodEndDate = _periodEndDate.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>(replay = 0)
    val toastEvent = _toastEvent.asSharedFlow()
    var year: Int = 0
    var month: Int = 0
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        toggleIsViewLoading()
        year = Calendar.getInstance().get(Calendar.YEAR)
        month = Calendar.getInstance().get(Calendar.MONTH).plus(1)
        getCurrentDayRangeUsingCalendar()
        getAttendanceDetails()
    }

    fun onPeriodTypeSelected(periodType: String){
        _periodTypeSelected.value = periodType
        when(periodType) {
            "Day" -> {
                getCurrentDayRangeUsingCalendar()
            }
            "Week" -> {
                getCurrentWeekRangeUsingCalendar()
            }
            "Month" -> {
                fillMonthPeriodCalendarMetadata()
            }
            "Custom" -> {

            }
        }
        getAttendanceDetails()
    }

    fun onDayPeriodDateSelected(timestamp: Long?){
        _dayPeriodDate.value = timestamp ?: 0
        _periodStartDateTimestamp.value = dayPeriodDate.value
        _periodEndDateTimestamp.value = dayPeriodDate.value + 86399990
        getAttendanceDetails()
    }

    fun onCustomStartPeriodDateSelected(timestamp: Long?){
        timestamp?.let {
            _periodStartDateTimestamp.value = timestamp
            getAttendanceDetails()
        }
    }

    fun onCustomEndPeriodDateSelected(timestamp: Long?){
        timestamp?.let {
            _periodEndDateTimestamp.value = timestamp
            getAttendanceDetails()
        }
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun addRegularisationAttendanceDetails(emailId: String){
        if (isViewLoading.value==false) toggleIsViewLoading()
        numberOfFetchProcess++
        appDataManager.addNRegularisationAttendanceData(emailId,_attendanceData.value,::addRegularisationDataResponse)
    }

    fun addRegularisationDataResponse(response: String){
        numberOfFetchProcess--
        if(response == "Success"){
            Log.d("addRegularisationDataResponse","Record Added $response")
            triggerToast("Record Added")
            clearAllFields()
        } else {
            //handle errors
            TODO()
            triggerToast("Error try again!")
        }
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
    }

    fun getAttendanceDetails(){
        if (appUserData.email.isNotBlank()) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFetchProcess++
            appDataManager.getFirebaseAttendanceData(periodStartDateTimestamp.value,periodEndDateTimestamp.value,appUserData.email,::updateAttendanceDetails)
        }
    }

    fun updateAttendanceDetails(attendanceData: QuerySnapshot?,response: String){
        numberOfFetchProcess--
        if(response == "Success"){
            _attendanceData.value.clear()
            _attendanceData.value.addAll(attendanceData
                ?.toObjects(AttendanceRegularisationData::class.java)
                ?.map {
                    it.copy(
                        regularisedCheckInTime = it.checkInTime,
                        regularisedCheckOutTime = it.checkOutTime,
                        regularisedTotalHours = it.totalHours,
                        status = "Pending"
                    )
                }
                ?: emptyList()
            )
            Log.d("LeaveRegularisationViewModel","_attendanceData called ${_attendanceData.value.size}")
            Log.d("LeaveRegularisationViewModel","_attendanceData called ${_attendanceData.value}")
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
    }

    fun checkInTimestampSelected(checkInTimestamp: Long?,attendanceId: Long,diffTimestamp: Long){
        checkInTimestamp?.let {
            _attendanceData.update { currentList ->
                currentList.map { data ->
                    if (data.date == attendanceId) {
                        data.copy(
                            regularisedCheckInTime = checkInTimestamp,
                            regularisedTotalHours = millisToRoundedHours(diffTimestamp)
                            )
                    } else {
                        data
                    }
                }.toMutableList()
            }
        }
    }

    fun checkOutTimestampSelected(checkOutTimestamp: Long?,attendanceId: Long,diffTimestamp: Long){
        checkOutTimestamp?.let {
            _attendanceData.update { currentList ->
                currentList.map { data ->
                    if (data.date == attendanceId) {
                        data.copy(
                            regularisedCheckOutTime = checkOutTimestamp,
                            regularisedTotalHours = millisToRoundedHours(diffTimestamp)
                        )
                    } else {
                        data
                    }
                }.toMutableList()
            }
        }
    }

    fun getCurrentWeekRangeUsingCalendar(){
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _periodStartDateTimestamp.value = calendar.timeInMillis
        _periodStartDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(periodStartDateTimestamp.value))
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        _periodEndDateTimestamp.value = calendar.timeInMillis
        _periodEndDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(periodEndDateTimestamp.value))
    }

    fun getCurrentDayRangeUsingCalendar(){
        val startOfTheDay = Calendar.getInstance()
        startOfTheDay.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _dayPeriodDate.value = startOfTheDay.timeInMillis
        _periodStartDateTimestamp.value = dayPeriodDate.value
        _periodEndDateTimestamp.value = dayPeriodDate.value + 86399990
    }

    fun fillMonthPeriodCalendarMetadata() {
//        val firstDayOfWeekMonth = getDayOfWeekOfMonth(year,month, 1)
//        _attendanceDayOfTheWeekIndex.value = daysOfWeek.indexOf(firstDayOfWeekMonth)
        _periodStartDateTimestamp.value = getStartOfMonthTimestamp(year,month)
        _periodStartDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(periodStartDateTimestamp.value))
        _periodEndDateTimestamp.value = getStartOfMonthTimestamp(year,month+1)
        _periodEndDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(periodEndDateTimestamp.value-86400000))
    }

    fun changeRegularisedDescription(newDescription: String,attendanceId: Long){
        _attendanceData.update { currentList ->
            currentList.map { data ->
                if (data.date == attendanceId) {
                    data.copy(regularisedDescription = newDescription)
                } else {
                    data
                }
            }.toMutableList()
        }
    }
    fun getStartOfMonthTimestamp(year: Int, month: Int): Long {
        val startOfMonth = LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.systemDefault()) // Converts to ZonedDateTime at midnight in the system's default time zone
        return startOfMonth.toInstant().toEpochMilli() // Returns the timestamp in milliseconds
    }

    fun getDayOfWeekOfMonth(year: Int, month: Int, day: Int): String {
        val firstDayOfMonth = LocalDate.of(year, month, day)
        return firstDayOfMonth.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    fun millisToRoundedHours(millis: Long): Float {
        val hours = millis / 1000.0 / 60.0 / 60.0
        return String.format(Locale.US,"%.2f", hours).toFloat()
    }

    fun clearAllFields(){
        onPeriodTypeSelected("Day")
        getCurrentDayRangeUsingCalendar()
        getAttendanceDetails()
    }

    fun triggerToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }
}