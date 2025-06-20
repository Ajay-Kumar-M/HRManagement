package com.example.hrmanagement.ui.userinfo

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.component.getAddressFromLocation
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AttendanceViewModel(application: Application): AndroidViewModel(application) {

    private val _attendanceFilterShowBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val attendanceFilterShowBottomSheet = _attendanceFilterShowBottomSheet.asStateFlow()
    private val _attendanceMonthShowModal: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val attendanceMonthShowModal = _attendanceMonthShowModal.asStateFlow()
    private val _attendanceSelectedViewType: MutableStateFlow<String> = MutableStateFlow("Week")
    val attendanceSelectedViewType = _attendanceSelectedViewType.asStateFlow()
    private val _attendanceStartDate: MutableStateFlow<String> = MutableStateFlow("")
    val attendanceStartDate = _attendanceStartDate.asStateFlow()
    private val _attendanceEndDate: MutableStateFlow<String> = MutableStateFlow("")
    val attendanceEndDate = _attendanceEndDate.asStateFlow()
    private var _attendanceTotalHours: MutableStateFlow<Float> = MutableStateFlow(0.0f)
    val attendanceTotalHours = _attendanceTotalHours.asStateFlow()
    private var _attendanceDataQuerySnapshot: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val attendanceDataQuerySnapshot = _attendanceDataQuerySnapshot.asStateFlow()
    private var _attendanceSelectedMonth: MutableStateFlow<Int> = MutableStateFlow(0)
    val attendanceSelectedMonth = _attendanceSelectedMonth.asStateFlow()
    private var _attendanceSelectedYear: MutableStateFlow<Int> = MutableStateFlow(0)
    val attendanceSelectedYear = _attendanceSelectedYear.asStateFlow()
    private var _attendanceDayOfTheWeekIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val attendanceDayOfTheWeekIndex = _attendanceDayOfTheWeekIndex.asStateFlow()
    private var _attendanceModalSelectedDate: MutableStateFlow<Int> = MutableStateFlow(0)
    val attendanceModalSelectedDate = _attendanceModalSelectedDate.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0
    val daysOfWeek: Array<String> = arrayOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
    private var attendanceStartDateTimestamp: Long = 0
    private var attendanceEndDateTimestamp: Long = 0
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        toggleIsViewLoading()
        _attendanceSelectedYear.value = Calendar.getInstance().get(Calendar.YEAR);
        _attendanceSelectedMonth.value = (Calendar.getInstance().get(Calendar.MONTH) + 1);
        if (attendanceSelectedViewType.value == "Week") {
            getCurrentWeekRangeUsingCalendar()
        } else {
            fillAttendanceCalendarMetadata()
        }
        getAttendanceDetails()
    }

    fun fillAttendanceCalendarMetadata() {
        val firstDayOfWeekMonth = getDayOfWeekOfMonth(attendanceSelectedYear.value,attendanceSelectedMonth.value, 1)
        _attendanceDayOfTheWeekIndex.value = daysOfWeek.indexOf(firstDayOfWeekMonth)
        attendanceStartDateTimestamp = getStartOfMonthTimestamp(attendanceSelectedYear.value,attendanceSelectedMonth.value)
        attendanceEndDateTimestamp = getStartOfMonthTimestamp(attendanceSelectedYear.value,attendanceSelectedMonth.value+1)
    }

    fun getAttendanceDetails(){
        if (appUserData.email.isNotBlank() == true) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFetchProcess++
            appDataManager.getFirebaseAttendanceData(attendanceStartDateTimestamp,attendanceEndDateTimestamp-10,appUserData.email,::updateAttendanceDetails)
        }
    }

    fun updateAttendanceDetails(attendanceData: QuerySnapshot?,response: String){
        numberOfFetchProcess--
        if(response == "Success"){
            Log.d("UserInfoScreenViewModel","updateAttendanceDetails called $attendanceData")
            var tempTotalHours = 0.0f
            attendanceData?.forEach { attendanceLog ->
                tempTotalHours += attendanceLog.data.getValue("totalHours").toString().toFloat()
            }
            _attendanceTotalHours.value = tempTotalHours
            _attendanceDataQuerySnapshot.value = attendanceData
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
    }

    fun updateUserSignInStatus(location: Location?, context: Context) {
        if (appUserData.email.isNotBlank() == true) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            var userLocation: String? = null
            val calendar = Calendar.getInstance()
            val startOfTheDay = Calendar.getInstance()
            startOfTheDay.apply {
                set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
                set(Calendar.MINUTE, 0)       // Set minutes to 0
                set(Calendar.SECOND, 0)       // Set seconds to 0
                set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
            }
            if (location != null) {
                viewModelScope.launch {
                    getAddressFromLocation(context, location.latitude, location.longitude) { address ->
                        userLocation = address
                        numberOfFetchProcess++
                        appDataManager.addSignInStatus(
                            appUserData.email,
                            calendar.timeInMillis,
                            startOfTheDay.timeInMillis,
                            userLocation ?: "",
                            ::updateSignInResponse
                        )
                    }
                }
            } else {
                numberOfFetchProcess++
                appDataManager.addSignInStatus(
                    appUserData.email,
                    calendar.timeInMillis,
                    startOfTheDay.timeInMillis,
                    "",
                    ::updateSignInResponse
                )
            }
        }
    }

    fun updateSignInResponse(response: String,error: String) {
        numberOfFetchProcess--
        if(response == "Success"){
            Log.d("UserInfoScreenViewModel","updateSignInResponse called $response")
            getAttendanceDetails()
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
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

    fun getDaysInMonth(year: Int, month: Int): Int {
        val date = LocalDate.of(year, month, 1)
        return date.lengthOfMonth()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun toggleAttendanceFilterShowBottomSheet(){
        _attendanceFilterShowBottomSheet.value = !_attendanceFilterShowBottomSheet.value
    }

    fun toggleAttendanceMonthShowModal(){
        _attendanceMonthShowModal.value = !_attendanceMonthShowModal.value
    }

    fun changeAttendanceViewType(viewType: String){
        _attendanceSelectedViewType.value = viewType
    }


    fun changeAttendanceMonthSelectedDate(changedDay: Int){
        _attendanceModalSelectedDate.value = changedDay
    }

    fun incrementAttendanceMonth(){
        if(_attendanceSelectedMonth.value == 12) {
            _attendanceSelectedMonth.value = 1
            _attendanceSelectedYear.value = _attendanceSelectedYear.value + 1
        } else {
            _attendanceSelectedMonth.value = _attendanceSelectedMonth.value + 1
        }
    }

    fun decrementAttendanceMonth(){
        if(_attendanceSelectedMonth.value == 0) {
            _attendanceSelectedMonth.value = 12
            _attendanceSelectedYear.value = _attendanceSelectedYear.value - 1
        } else {
            _attendanceSelectedMonth.value = _attendanceSelectedMonth.value - 1
        }
    }

    fun decrementAttendanceWeek(){
        if (isViewLoading.value==false) toggleIsViewLoading()
        attendanceStartDateTimestamp = attendanceStartDateTimestamp - 604800000
        _attendanceStartDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(attendanceStartDateTimestamp))
        attendanceEndDateTimestamp = attendanceEndDateTimestamp - 604800000
        _attendanceEndDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(attendanceEndDateTimestamp))
    }

    fun incrementAttendanceWeek(){
        if (isViewLoading.value==false) toggleIsViewLoading()
        attendanceStartDateTimestamp = attendanceStartDateTimestamp + 604800000
        _attendanceStartDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(attendanceStartDateTimestamp))
        attendanceEndDateTimestamp = attendanceEndDateTimestamp + 604800000
        _attendanceEndDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(attendanceEndDateTimestamp))
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
        attendanceStartDateTimestamp = calendar.timeInMillis
        _attendanceStartDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(attendanceStartDateTimestamp))
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        attendanceEndDateTimestamp = calendar.timeInMillis
        _attendanceEndDate.value = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(attendanceEndDateTimestamp))
    }
}