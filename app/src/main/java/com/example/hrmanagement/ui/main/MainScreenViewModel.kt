package com.example.hrmanagement.ui.main

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.AnnouncementData
import com.example.hrmanagement.data.AnnouncementList
import com.example.hrmanagement.data.AttendanceData
import com.example.hrmanagement.data.CommentsData
import com.example.hrmanagement.data.DepartmentInfo
import com.example.hrmanagement.data.GoogleAuth
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.data.LikeData
import com.example.hrmanagement.data.LinkData
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class MainScreenViewModel: ViewModel(),DefaultLifecycleObserver {

    val userImageUriUiState = appPreferenceDataStore.userImageURLFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    var userEmailUiState: String?
    private var _liveLeaveTrackerDetails: MutableStateFlow<LeaveTrackerData> = MutableStateFlow(LeaveTrackerData())
    val liveLeaveTrackerDetails = _liveLeaveTrackerDetails.asStateFlow()
    private var _liveUserDetails: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
    val liveUserDetails = _liveUserDetails.asStateFlow()
    private var _userAttendanceData: MutableStateFlow<AttendanceData> = MutableStateFlow(AttendanceData())
    val userAttendanceData = _userAttendanceData.asStateFlow()
    private var _quickLinksLimitedData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val quickLinksLimitedData = _quickLinksLimitedData.asStateFlow()
    private var _announcementsLimitedData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val announcementsLimitedData = _announcementsLimitedData.asStateFlow()
    private var _holidaysData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val holidaysData = _holidaysData.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _addTaskShowBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val addTaskShowBottomSheet = _addTaskShowBottomSheet.asStateFlow()
    private var numberOfFeatchProcess: Int = 0
    private var calendarYear: Int = 0

    init {
        toggleIsViewLoading()
        runBlocking {
            userEmailUiState = appPreferenceDataStore.emailFlow.firstOrNull()
        }
        calendarYear = Calendar.getInstance().get(Calendar.YEAR);
        fetchUserDetails()
        fetchUserSignInStatus()
        fetchLimitedQuickLinks()
        fetchLimitedAnnouncements()
        getLeaveTrackerDetails()
        getHolidayDetails()
        if (!userEmailUiState.isNullOrBlank()){
            appDataManager.listenForUserSignInStatusUpdates(userEmailUiState!!)
        }
//        viewModelScope.launch {
//
//        }
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        if(userEmailUiState!=null) {
            appDataManager.getFirebaseUser(userEmailUiState!!,::updateUserDetails)
        }
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String){
        numberOfFeatchProcess--
        Log.d("MainScreenViewModel","updateUserDetails called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            _liveUserDetails.value = userDetails
//            viewModelScope.launch {
//                appPreferenceDataStore.updateUserDetails(UserLoginData.from(userDetails))
//            }
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun updateUserSignInStatus(){
        if ((userEmailUiState!=null)&&((userEmailUiState?.isNotBlank())==true)) {
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
            appDataManager.addSignInStatus(userEmailUiState!!,calendar.timeInMillis,startOfTheDay.timeInMillis,::updateSignInResponse)
        }
    }

    fun updateSignInResponse(response: String,error: String) {
        numberOfFeatchProcess--
        if(response == "Success"){
            Log.d("MainScreenViewModel","updateSignInResponse called $response")
        } else {
            //handle errors
            TODO()
        }
        fetchUserSignInStatus()
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun fetchUserSignInStatus() {
        val startOfTheDay = Calendar.getInstance()
        startOfTheDay.apply {
            set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
            set(Calendar.MINUTE, 0)       // Set minutes to 0
            set(Calendar.SECOND, 0)       // Set seconds to 0
            set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
        }
        if (!userEmailUiState.isNullOrBlank()){
            if (!_isViewLoading.value) {
                toggleIsViewLoading()
            }
            numberOfFeatchProcess++
            appDataManager.getFirebaseAttendanceData(startOfTheDay.timeInMillis,startOfTheDay.timeInMillis+86399990,userEmailUiState!!,::updateAttendanceDetails)
        }
    }

    fun updateAttendanceDetails(attendanceData: QuerySnapshot?,response: String){
        numberOfFeatchProcess--
        if(response == "Success"){
            Log.d("UserInfoScreenViewModel","updateAttendanceDetails called $attendanceData")
            val documentSnapshot = attendanceData?.first()
            if(documentSnapshot?.exists() == true) {
                _userAttendanceData.value = documentSnapshot.toObject(AttendanceData::class.java)
            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun fetchLimitedQuickLinks(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.getLimitedQuickLinks(3L,::updateLimitedQuickLinksData)

    }

    fun updateLimitedQuickLinksData(quickLinksData: QuerySnapshot?,response: String){
        numberOfFeatchProcess--
        if(response == "Success"){
            Log.d("MainScreenViewModel","updateQuickLinksData called $quickLinksData")
            quickLinksData?.count()?.let {
                if(it > 0) {
                    _quickLinksLimitedData.value = quickLinksData
                }
            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }


    fun fetchLimitedAnnouncements(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.getLimitedAnnouncements(3L,::updateLimitedAnnouncementsData)

    }

    fun updateLimitedAnnouncementsData(announcementData: QuerySnapshot?,response: String){
        numberOfFeatchProcess--
        if(response == "Success"){
            Log.d("MainScreenViewModel","updateQuickLinksData called $announcementData")
            announcementData?.count()?.let {
                if(it > 0) {
                    _announcementsLimitedData.value = announcementData
                }
            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun getLeaveTrackerDetails(){
        if ((userEmailUiState!=null)&&((userEmailUiState?.isNotBlank())==true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFeatchProcess++
            appDataManager.getFirebaseLeaveTrackerData(calendarYear, userEmailUiState!!,::updateLeaveTrackerData)
        }
    }

    fun updateLeaveTrackerData(leaveTrackerData: LeaveTrackerData?, response: String){
        Log.d("MainScreenViewModel","updateLeaveTrackerData called $leaveTrackerData")
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

    fun getHolidayDetails(){
        if (isViewLoading.value==false) toggleIsViewLoading()
        numberOfFeatchProcess++
        appDataManager.getHolidays(::updateHolidayData)
    }

    fun updateHolidayData(holidays: QuerySnapshot?, response: String){
        Log.d("MainScreenViewModel","updateHolidayData called $holidays")
        numberOfFeatchProcess--
        if((response == "Success")&&(holidays!=null)){
            _holidaysData.value = holidays
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleAddTaskShowBottomSheet(){
        _addTaskShowBottomSheet.value = !_addTaskShowBottomSheet.value
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun addUserToDB(){
        for(i in 2..10) {

            appDataManager.addAnnouncementTemp(
                AnnouncementList(
                    i,
                    "Announcement Title $i",
                    Calendar.getInstance().timeInMillis,
                    "General Awarness",
                    1,
                    3,
                    "ajay.kumar0495@gmail.com",
                    "Ajay Kumar M",
                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
                    false,
                    "Chennai"
                )
            )

            appDataManager.addAnnouncementDataTemp(
                AnnouncementData(
                    i,
                "Announcement Title $i",
                Calendar.getInstance().timeInMillis,
                "General Awarness",
                "Chennai",
                "Announcement Message",
                "https://www.google.com/",
                0,
                "Active",
                false,
                true,
                3,
                1,
                1,
                3,
                    mutableMapOf(
                    Pair("1",
                        LikeData(
                            "Ajay Kumar M",
                            "ajay.kumar0495@gmail.com",
                            "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                        )
                    )
                ),
                    mutableMapOf(
                    Pair(
                        "1",
                        CommentsData(
                            1,
                            "Dummy User 1",
                            "dummayuser1@gmail.com",
                            "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
                            Calendar.getInstance().timeInMillis,
                            1,
                            mutableMapOf(
                                Pair("1",
                                    LikeData(
                                        "Dummy User 1",
                                        "dummayuser1@gmail.com",
                                        "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                    )
                                )
                            ),
                            "Announcement comment 1",
                            1
                        )
                    ),
                    Pair(
                        "2",
                        CommentsData(
                            2,
                            "Dummy User 2",
                            "dummayuser2@gmail.com",
                            "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
                            Calendar.getInstance().timeInMillis,
                            1,
                            mutableMapOf(
                                Pair("1",
                                    LikeData(
                                        "Dummy User 2",
                                        "dummayuser2@gmail.com",
                                        "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                    )
                                )
                            ),
                            "Announcement comment 2",
                            1
                        )
                    ),
                    Pair(
                        "3",
                        CommentsData(
                            3,
                            "Dummy User 3",
                            "dummayuser3@gmail.com",
                            "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
                            Calendar.getInstance().timeInMillis,
                            1,
                            mutableMapOf(
                                Pair("1",
                                    LikeData(
                                        "Dummy User 3",
                                        "dummayuser3@gmail.com",
                                        "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                    )
                                )
                            ),
                            "Announcement comment 3",
                            1
                        )
                    )
                ),
                "ajay.kumar0495@gmail.com",
                "Ajay Kumar M",
                "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
            )
            )


        }
    }

    fun logoutCurrentUser(){
        viewModelScope.launch {
            MyApplication.googleAuthenticationService.logout()
        }
    }

    fun tempupdatedb(){
        viewModelScope.launch {
            appPreferenceDataStore.updateUserDetails(
                UserLoginData(
                    "google.com",
                    "eyJhbGciOiJSUzI1NiIsImtpZCI6ImZlNjVjY2I4ZWFkMGJhZWY1ZmQzNjE5NWQ2NTI4YTA1NGZiYjc2ZjMiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiQWpheSBrdW1hciIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQ2c4b2NKVEdEN1hQdkxON0hHV3ZIN1ZCYnNzZ1IyRUFXYzVuN183RDVfNkZiZVpJX19aeGV1az1zOTYtYyIsImlzcyI6Imh0dHBzOi8vc2VjdXJldG9rZW4uZ29vZ2xlLmNvbS9ocm1hbmFnZW1lbnQtNjNjNmQiLCJhdWQiOiJocm1hbmFnZW1lbnQtNjNjNmQiLCJhdXRoX3RpbWUiOjE3NDgzMjY1NzIsInVzZXJfaWQiOiJFQ1JqZzhGUnA0YUpCM2huSnIwVVpaUnZJUmoyIiwic3ViIjoiRUNSamc4RlJwNGFKQjNobkpyMFVaWlJ2SVJqMiIsImlhdCI6MTc0ODMyNjU4MywiZXhwIjoxNzQ4MzMwMTgzLCJlbWFpbCI6ImFqYXkua3VtYXIwNDk1QGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7Imdvb2dsZS5jb20iOlsiMTE4MjU3MTk3MTIwNjkxNTIyNzQ1Il0sImVtYWlsIjpbImFqYXkua3VtYXIwNDk1QGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6Imdvb2dsZS5jb20ifX0.ZlVGFktq8Yf7CWzdv5jKN0UaNUgKFPlJXt2Z4xymtoa8LZRP4rSyjzFF33QJH0f5-L1aqiXLWUi71_gV-d2wsBoV6-ikdogI72NUYlj_Gp5p5WJ7ijWCLplwSxSRzIuSN8eLDVNIF1UpcGCM9ayuLDBxX3zEl_GwJ9M1ZcuViUGZy63Vzpa_kO6gvtZNnBfwtDxsJ4Ymj0G5sCa2GawWZ0LDqr6_zV4TEOMnOsDeQZSynvKYfhY-7JQGh9ZKPm9D32zIvSaH6D_b9RrzZc0_9uPhujgG6_qQ38YEvVw9qxDmhxbZETY9xRIbLbNyjGO2FGR6YMb0PTJEi0pLRmVTxA",
                    "Ajay Kumar",
                    "ajay.kumar0495@gmail.com",
                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
                    "",
                    "",
                    "",
                    profileUrl = TODO(),
                    "Department1"
                )
            )
        }
    }

}

/*

fun search() {
        toggleCircularProgressIndicator()
        if(contactSearchString.text.isNotEmpty())
        {
            _filterContact.clear()
            _filterContact.addAll(
                deviceContactsList.filter {
                    it.displayName.contains(contactSearchString.text, true) or it.phoneNumber.contains(contactSearchString.text, true)
                }
            )

        }
        toggleCircularProgressIndicator()
    }

    private var _filterContact = mutableListOf<Contact>().toMutableStateList()
    val filterContact: List<Contact>
        get() = _filterContact.toList()

    private var deviceContactsList: MutableList<Contact> = mutableListOf()


====================

//            appDataManager.addDummyLinkData(LinkData(
//                "Link$i",
//                "www.google.com",
//                "Active"
//            ))

===================


//            appDataManager.addGoogleAuthUserData(GoogleAuth("google.com",
//                "",
//                "Dummy User $i",
//                "dummayuser${i}@gmail.com",
//                "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
//                "1234${i}",
//                "Active",
//                "www.google.com",
//                "Department1")
//            )

//            appDataManager.addDepartmentData(
//                DepartmentInfo(
//                    "Sub1Sub1Department${i}",
//                    "5",
//                    "Active",
//                    mapOf(
//                        Pair(
//                            "Person1", mapOf(
//                                Pair("Name", "Sub1Sub1Department${i}Person1"),
//                                Pair("Designation", "Employee"),
//                                Pair("Employee ID", "${i}1001"),
//                                Pair("Email ID", "Sub1Sub1Department${i}Person1@company.com"),
//                                Pair("Mobile Number", "1234"),
//                                Pair(
//                                    "Profile Image",
//                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
//                                ),
//                                Pair("Profile URL", "www.google.com"),
//                                Pair("Department", "Sub1Sub1Department${i}")
//                            )
//                        ),
//                        Pair(
//                            "Person2", mapOf(
//                                Pair("Name", "Sub1Sub1Department${i}Person2"),
//                                Pair("Designation", "Employee"),
//                                Pair("Employee ID", "${i}1001"),
//                                Pair("Email ID", "Sub1Sub1Department${i}Person2@company.com"),
//                                Pair("Mobile Number", "1234"),
//                                Pair(
//                                    "Profile Image",
//                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
//                                ),
//                                Pair("Profile URL", "www.google.com"),
//                                Pair("Department", "Sub1Sub1Department${i}")
//                            )
//                        ),
//                        Pair(
//                            "Person3", mapOf(
//                                Pair("Name", "Sub1Sub1Department${i}Person3"),
//                                Pair("Designation", "Employee"),
//                                Pair("Employee ID", "${i}1001"),
//                                Pair("Email ID", "Sub1Sub1Department${i}Person3@company.com"),
//                                Pair("Mobile Number", "1234"),
//                                Pair(
//                                    "Profile Image",
//                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
//                                ),
//                                Pair("Profile URL", "www.google.com"),
//                                Pair("Department", "Sub1Sub1Department${i}")
//                            )
//                        ),
//                        Pair(
//                            "Person4", mapOf(
//                                Pair("Name", "Sub1Sub1Department${i}Person4"),
//                                Pair("Designation", "Employee"),
//                                Pair("Employee ID", "${i}1001"),
//                                Pair("Email ID", "Sub1Sub1Department${i}Person4@company.com"),
//                                Pair("Mobile Number", "1234"),
//                                Pair(
//                                    "Profile Image",
//                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
//                                ),
//                                Pair("Profile URL", "www.google.com"),
//                                Pair("Department", "Sub1Sub1Department${i}")
//                            )
//                        ),
//                        Pair(
//                            "Person5", mapOf(
//                                Pair("Name", "Sub1Sub1Department${i}Person5"),
//                                Pair("Designation", "Employee"),
//                                Pair("Employee ID", "${i}1001"),
//                                Pair("Email ID", "Sub1Sub1Department${i}Person5@company.com"),
//                                Pair("Mobile Number", "1234"),
//                                Pair(
//                                    "Profile Image",
//                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
//                                ),
//                                Pair("Profile URL", "www.google.com"),
//                                Pair("Department", "Sub1Sub1Department${i}")
//                            )
//                        ),
//                    )
//                )
//            )

======================

//        appDataManager.addUserSignInStatusTemp("ajay.kumar0495@gmail.com", UserSignInStatusData(
//            "Checked-Out",
//            0,
//            0
//        ))

//        appDataManager.addSignInStatus(
//            "ajay.kumar0495@gmail.com",
//            UserSignInStatusData(
//                "Checked-Out",
//                0,
//                0
//            )
//        )
//        val startofthemonth = Calendar.getInstance()
//        startofthemonth.apply {
//            set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month
//            set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
//            set(Calendar.MINUTE, 0)       // Set minutes to 0
//            set(Calendar.SECOND, 0)       // Set seconds to 0
//            set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
//        }
//        var startDate = startofthemonth.timeInMillis
//
//        for(i in 1..28) {
//            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = startDate
//            val day = calendar.get(Calendar.DAY_OF_MONTH)
//            val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
//            val year = calendar.get(Calendar.YEAR)
//            appDataManager.addAttendanceData(
//                AttendanceData(
//                    startDate,
//                    (startDate+21600000),
//                    (startDate+64800000),
//                    "Chennai",
//                    "Chennai",
//                    "Check-in note",
//                    "Check-out note",
//                    "ajay.kumar0495@gmail.com",
//                    "Present",
//                    12.30,
//                    day,
//                    month,
//                    year,
//                    ""
//                )
//            )
//            startDate = startDate+86400000
//        }

= appPreferenceDataStore.emailFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        appDataManager.addGoogleAuthUserData(GoogleAuth("google.com",
            "eyJhbGciOiJSUzI1NiIsImtpZCI6ImZlNjVjY2I4ZWFkMGJhZWY1ZmQzNjE5NWQ2NTI4YTA1NGZiYjc2ZjMiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiQWpheSBrdW1hciIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQ2c4b2NKVEdEN1hQdkxON0hHV3ZIN1ZCYnNzZ1IyRUFXYzVuN183RDVfNkZiZVpJX19aeGV1az1zOTYtYyIsImlzcyI6Imh0dHBzOi8vc2VjdXJldG9rZW4uZ29vZ2xlLmNvbS9ocm1hbmFnZW1lbnQtNjNjNmQiLCJhdWQiOiJocm1hbmFnZW1lbnQtNjNjNmQiLCJhdXRoX3RpbWUiOjE3NDgzMjY1NzIsInVzZXJfaWQiOiJFQ1JqZzhGUnA0YUpCM2huSnIwVVpaUnZJUmoyIiwic3ViIjoiRUNSamc4RlJwNGFKQjNobkpyMFVaWlJ2SVJqMiIsImlhdCI6MTc0ODMyNjU4MywiZXhwIjoxNzQ4MzMwMTgzLCJlbWFpbCI6ImFqYXkua3VtYXIwNDk1QGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7Imdvb2dsZS5jb20iOlsiMTE4MjU3MTk3MTIwNjkxNTIyNzQ1Il0sImVtYWlsIjpbImFqYXkua3VtYXIwNDk1QGdtYWlsLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6Imdvb2dsZS5jb20ifX0.ZlVGFktq8Yf7CWzdv5jKN0UaNUgKFPlJXt2Z4xymtoa8LZRP4rSyjzFF33QJH0f5-L1aqiXLWUi71_gV-d2wsBoV6-ikdogI72NUYlj_Gp5p5WJ7ijWCLplwSxSRzIuSN8eLDVNIF1UpcGCM9ayuLDBxX3zEl_GwJ9M1ZcuViUGZy63Vzpa_kO6gvtZNnBfwtDxsJ4Ymj0G5sCa2GawWZ0LDqr6_zV4TEOMnOsDeQZSynvKYfhY-7JQGh9ZKPm9D32zIvSaH6D_b9RrzZc0_9uPhujgG6_qQ38YEvVw9qxDmhxbZETY9xRIbLbNyjGO2FGR6YMb0PTJEi0pLRmVTxA",
            "Ajay Kumar",
            "ajay.kumar0495@gmail.com",
            "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
            "1234",
            "Success",
            "www.google.com"))

appDataManager.addDepartmentData(
                DepartmentInfo(
                    "Department$i",
                    "50",
                    "",
                    mapOf(
                        Pair(
                            "Person1", mapOf(
                                Pair("Name", "Person1"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "1001"),
                                Pair("Email ID", "person1@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com")
                            )
                        ),
                        Pair(
                            "Person2", mapOf(
                                Pair("Name", "Person2"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "1002"),
                                Pair("Email ID", "person2@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com")
                            )
                        ),
                        Pair(
                            "Person3", mapOf(
                                Pair("Name", "Person3"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "1003"),
                                Pair("Email ID", "person3@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com")
                            )
                        ),
                        Pair(
                            "Person4", mapOf(
                                Pair("Name", "Person4"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "1004"),
                                Pair("Email ID", "person4@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com")
                            )
                        ),
                        Pair(
                            "Person5", mapOf(
                                Pair("Name", "Person5"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "1005"),
                                Pair("Email ID", "person5@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com")
                            )
                        )
                    )
                )
            )



            appDataManager.addLeaveTrackerDataTemp(
                LeaveTrackerData(
                    "ajay.kumar0495@gmail.com",
                    2025,
                    1001,
                    5,
                    8,4,8,4,8,4,8,4,8,4,
                    mutableMapOf(
                        Pair("1001_1",
                            mapOf(
                                Pair("Leave ID","1"),
                                Pair("Leave Type","Casual Leave"),
                                Pair("Number Of Days","1"),
                                Pair("Start Date","23-Jan-2025"),
                                Pair("End Date","23-Jan-2025"),
                                Pair("Status","Approved"),
                                Pair("Email","ajay.kumar0495@gmail.com"),
                                Pair("Employee ID","1001"),
                                Pair("Employee Name","Ajay Kumar M"),
                                Pair("Team Email Id","team@gmail.com"),
                                Pair("Date Of Request","20-Jan-2025"),
                                Pair("Reason For Leave","Reason")
                            )
                        ),
                        Pair("1001_2",
                            mapOf(
                                Pair("Leave ID","2"),
                                Pair("Leave Type","Sick Leave"),
                                Pair("Number Of Days","2"),
                                Pair("Start Date","23-Feb-2025"),
                                Pair("End Date","24-Feb-2025"),
                                Pair("Status","Approved"),
                                Pair("Email","ajay.kumar0495@gmail.com"),
                                Pair("Employee ID","1001"),
                                Pair("Employee Name","Ajay Kumar M"),
                                Pair("Team Email Id","team@gmail.com"),
                                Pair("Date Of Request","20-Feb-2025"),
                                Pair("Reason For Leave","Reason")
                            )
                        ),
                        Pair("1001_3",
                            mapOf(
                                Pair("Leave ID","3"),
                                Pair("Leave Type","On Duty"),
                                Pair("Number Of Days","1"),
                                Pair("Start Date","23-Mar-2025"),
                                Pair("End Date","23-Mar-2025"),
                                Pair("Status","Approved"),
                                Pair("Email","ajay.kumar0495@gmail.com"),
                                Pair("Employee ID","1001"),
                                Pair("Employee Name","Ajay Kumar M"),
                                Pair("Team Email Id","team@gmail.com"),
                                Pair("Date Of Request","20-Mar-2025"),
                                Pair("Reason For Leave","Reason")
                            )
                        ),
                        Pair("1001_4",
                            mapOf(
                                Pair("Leave ID","4"),
                                Pair("Leave Type","Optional Holiday"),
                                Pair("Number Of Days","1"),
                                Pair("Start Date","23-Apr-2025"),
                                Pair("End Date","23-Apr-2025"),
                                Pair("Status","Approved"),
                                Pair("Email","ajay.kumar0495@gmail.com"),
                                Pair("Employee ID","1001"),
                                Pair("Employee Name","Ajay Kumar M"),
                                Pair("Team Email Id","team@gmail.com"),
                                Pair("Date Of Request","20-Mar-2025"),
                                Pair("Reason For Leave","Reason")
                            )
                        ),
                        Pair("1001_5",
                            mapOf(
                                Pair("Leave ID","5"),
                                Pair("Leave Type","Comp Off"),
                                Pair("Number Of Days","1"),
                                Pair("Start Date","23-May-2025"),
                                Pair("End Date","23-May-2025"),
                                Pair("Status","Not Approved"),
                                Pair("Email","ajay.kumar0495@gmail.com"),
                                Pair("Employee ID","1001"),
                                Pair("Employee Name","Ajay Kumar M"),
                                Pair("Team Email Id","team@gmail.com"),
                                Pair("Date Of Request","20-May-2025"),
                                Pair("Reason For Leave","Reason")
                            )
                        )
                    ),
                    ""
                ),
                i
            )


appDataManager.addGoalData(
                GoalData(
                    "Goal$i",
                    "${Calendar.getInstance().timeInMillis}",
                    "${Calendar.getInstance().timeInMillis+86400000}",
                    "Low",
                    "goal description",
                    15,
                    "ajay.kumar0495@gmail.com",
                    mapOf(
                        Pair("1", "comment1"),
                        Pair("2", "comment2"),
                        Pair("3", "comment3")
                    )
                ),
                i,
                true
            )


 */