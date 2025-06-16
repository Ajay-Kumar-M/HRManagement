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
    var userEmailId: String? = ""
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
//    var _liveUserDetails: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
//    val liveUserDetails = _liveUserDetails.asStateFlow()
    private var _liveDepartmentDetails: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val liveDepartmentDetails = _liveDepartmentDetails.asStateFlow()
    var numberOfFeatchProcess: Int = 0

    init {
        toggleIsViewLoading()
        runBlocking {
            userEmailId = appPreferenceDataStore.emailFlow.firstOrNull()
        }
        fetchUserDetails()
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

    fun getDayOfWeekOfMonth(year: Int, month: Int, day: Int): String {
        val firstDayOfMonth = LocalDate.of(year, month, day)
        return firstDayOfMonth.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    fun getDaysInMonth(year: Int, month: Int): Int {
        val date = LocalDate.of(year, month, 1)
        return date.lengthOfMonth()
    }

    fun getDepartmentDetails(departmentName: String){
        if (isViewLoading.value==false) toggleIsViewLoading()
        numberOfFeatchProcess++
        appDataManager.getFirebaseDepartment(departmentName,::updateDepartmentDetails)
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

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
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