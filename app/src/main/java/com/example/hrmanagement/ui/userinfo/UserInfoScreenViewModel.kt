package com.example.hrmanagement.ui.userinfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.AttendanceData
import com.example.hrmanagement.data.DepartmentInfo
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DayOfWeek
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.reflect.full.memberProperties

class UserInfoScreenViewModel: ViewModel() {

    val userImageUriFlowState = appPreferenceDataStore.userImageURLFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    private val _userLoginData: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
    val userLoginData = _userLoginData.asStateFlow()
    private val _leaveTrackerShowBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val leaveTrackerShowBottomSheet = _leaveTrackerShowBottomSheet.asStateFlow()
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
    private val _showBottomSheetLeaveType: MutableStateFlow<String> = MutableStateFlow("")
    val showBottomSheetLeaveType = _showBottomSheetLeaveType.asStateFlow()
    var userEmailId: String? = ""
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
//    var _liveUserDetails: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
//    val liveUserDetails = _liveUserDetails.asStateFlow()
    private var _liveLeaveTrackerDetails: MutableStateFlow<LeaveTrackerData> = MutableStateFlow(LeaveTrackerData())
    val liveLeaveTrackerDetails = _liveLeaveTrackerDetails.asStateFlow()
    private var _liveDepartmentDetails: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val liveDepartmentDetails = _liveDepartmentDetails.asStateFlow()
    private var _calendarYear: MutableStateFlow<Int> = MutableStateFlow(0)
    val calendarYear = _calendarYear.asStateFlow()
    private var _goalsQuerySnapshot: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val goalsQuerySnapshot = _goalsQuerySnapshot.asStateFlow()
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
    private var attendanceStartDateTimestamp: Long = 0
    private var attendanceEndDateTimestamp: Long = 0
    var numberOfFeatchProcess: Int = 0
    val daysOfWeek: Array<String> = arrayOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    init {
        toggleIsViewLoading()
        runBlocking {
            userEmailId = appPreferenceDataStore.emailFlow.firstOrNull()
        }
        fetchUserDetails()
        _calendarYear.value = Calendar.getInstance().get(Calendar.YEAR);
        _attendanceSelectedYear.value = _calendarYear.value
        _attendanceSelectedMonth.value = (Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        userEmailId?.let { appDataManager.getFirebaseUser(it,::updateUserDetails) }
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String){
        numberOfFeatchProcess--
        Log.d("MainScreenViewModel","updateUserDetails called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            _userLoginData.value = userDetails
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun fillAttendanceCalendarMetadata() {
        val firstDayOfWeekMonth = getDayOfWeekOfMonth(attendanceSelectedYear.value,attendanceSelectedMonth.value, 1)
        _attendanceDayOfTheWeekIndex.value = daysOfWeek.indexOf(firstDayOfWeekMonth)
        attendanceStartDateTimestamp = getStartOfMonthTimestamp(attendanceSelectedYear.value,attendanceSelectedMonth.value)
        attendanceEndDateTimestamp = getStartOfMonthTimestamp(attendanceSelectedYear.value,attendanceSelectedMonth.value+1)
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

    fun updateUserSignInStatus(){
        if ((userEmailId!=null)&&((userEmailId?.isNotBlank())==true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            val calendar = Calendar.getInstance()
            val startOfTheDay = Calendar.getInstance()
            startOfTheDay.apply {
                set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
                set(Calendar.MINUTE, 0)       // Set minutes to 0
                set(Calendar.SECOND, 0)       // Set seconds to 0
                set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
            }
            numberOfFeatchProcess++
            appDataManager.addSignInStatus(userEmailId!!,calendar.timeInMillis,startOfTheDay.timeInMillis,::updateSignInResponse)
        }
    }

    fun updateSignInResponse(response: String,error: String) {
        numberOfFeatchProcess--
        if(response == "Success"){
            Log.d("UserInfoScreenViewModel","updateSignInResponse called $response")
            getAttendanceDetails()
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }


    fun getDepartmentDetails(departmentName: String){
        if (isViewLoading.value==false) toggleIsViewLoading()
        numberOfFeatchProcess++
        appDataManager.getFirebaseDepartment(departmentName,::updateDepartmentDetails)
    }

    fun getGoals(){
        if ((userEmailId!=null)&&((userEmailId?.isNotBlank())==true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFeatchProcess++
            appDataManager.getUserGoalsData(userEmailId!!,::updateUserGoalsData)
        }
    }

    fun getAttendanceDetails(){
        if ((userEmailId!=null)&&((userEmailId?.isNotBlank())==true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFeatchProcess++
            appDataManager.getFirebaseAttendanceData(attendanceStartDateTimestamp,attendanceEndDateTimestamp,userEmailId!!,::updateAttendanceDetails)
        }
    }

    fun updateAttendanceDetails(attendanceData: QuerySnapshot?,response: String){
        numberOfFeatchProcess--
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
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun getLeaveTrackerDetails(){
        if ((userEmailId!=null)&&((userEmailId?.isNotBlank())==true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFeatchProcess++
            appDataManager.getFirebaseLeaveTrackerData(calendarYear.value, userEmailId!!,::updateLeaveTrackerData)
        }
    }

    fun updateDepartmentDetails(departmentInfoQuerySnapshot: QuerySnapshot?, response: String){
        Log.d("UserInfoScreenViewModel","updateDepartmentDetails called ${departmentInfoQuerySnapshot?.size()}")
        numberOfFeatchProcess--
        if((response == "Success")&&(departmentInfoQuerySnapshot!=null)){
            _liveDepartmentDetails.value = departmentInfoQuerySnapshot
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun updateUserGoalsData(goalsData: QuerySnapshot?, status: String){
        Log.d("UserInfoScreenViewModel","updateLeaveTrackerData called $goalsData")
        numberOfFeatchProcess--
        if(status == "Success"){
//            _liveLeaveTrackerDetails.value = leaveTrackerData
            _goalsQuerySnapshot.value = goalsData
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun updateLeaveTrackerData(leaveTrackerData: LeaveTrackerData?, response: String){
        Log.d("UserInfoScreenViewModel","updateLeaveTrackerData called $leaveTrackerData")
        numberOfFeatchProcess--
        if((response == "Success")&&(leaveTrackerData!=null)){
            _liveLeaveTrackerDetails.value = leaveTrackerData
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleLeaveTrackerShowBottomSheet(){
        _leaveTrackerShowBottomSheet.value = !_leaveTrackerShowBottomSheet.value
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

    fun changeShowBottomSheetLeaveType(leaveType: String){
        _showBottomSheetLeaveType.value = leaveType
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun incrementCalendarYear(){
        _calendarYear.value = _calendarYear.value+1
    }

    fun decrementCalendarYear(){
        _calendarYear.value = _calendarYear.value-1
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

fun getAllFieldsAndValues(obj: Any): List<Pair<String, Any?>> {
    return obj::class.memberProperties
        .associate { prop ->
            prop.name to prop.getter.call(obj)
        }
        .toList()
}


/*

//        val firstDayOfMonth = LocalDate.now().withDayOfMonth(1)

//        val startOfTheDay = Calendar.getInstance()
//        startOfTheDay.apply {
//            set(Calendar.YEAR, attendanceSelectedYear.value)
//            set(Calendar.MONTH, attendanceSelectedMonth.value)
//            set(Calendar.DATE, 1)
//            set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
//            set(Calendar.MINUTE, 0)       // Set minutes to 0
//            set(Calendar.SECOND, 0)       // Set seconds to 0
//            set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
//        }

//val temp = startDate.time
//        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")
//    return Pair(dateFormat.format(startDate), dateFormat.format(endDate))
 */