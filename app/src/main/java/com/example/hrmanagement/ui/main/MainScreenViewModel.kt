package com.example.hrmanagement.ui.main

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.Service.MyApplication.Companion.appUserEmailId
import com.example.hrmanagement.component.getAddressFromLocation
import com.example.hrmanagement.component.startOfTheDayInMillis
import com.example.hrmanagement.data.AnnouncementData
import com.example.hrmanagement.data.AnnouncementList
import com.example.hrmanagement.data.AttendanceData
import com.example.hrmanagement.data.CommentsData
import com.example.hrmanagement.data.DepartmentInfo
import com.example.hrmanagement.data.GoalData
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.data.LikeData
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    val userImageUriUiState = appPreferenceDataStore.userImageURLFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    var userEmailUiState: String?
    private var _liveLeaveTrackerDetails: MutableStateFlow<LeaveTrackerData> = MutableStateFlow(LeaveTrackerData())
    val liveLeaveTrackerDetails = _liveLeaveTrackerDetails.asStateFlow()
    private var _liveUserDetails: MutableStateFlow<UserLoginData> =
        MutableStateFlow(UserLoginData())
    val liveUserDetails = _liveUserDetails.asStateFlow()
    private var _userAttendanceData: MutableStateFlow<AttendanceData> = MutableStateFlow(AttendanceData())
    val userAttendanceData = _userAttendanceData.asStateFlow()
    private var _favouritesLimitedData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val favouritesLimitedData = _favouritesLimitedData.asStateFlow()
    private var _quickLinksLimitedData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val quickLinksLimitedData = _quickLinksLimitedData.asStateFlow()
    private var _announcementsLimitedData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val announcementsLimitedData = _announcementsLimitedData.asStateFlow()
    private var _holidaysData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val holidaysData = _holidaysData.asStateFlow()
    private var _isSignInViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSignInViewLoading = _isSignInViewLoading.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _addTaskShowBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val addTaskShowBottomSheet = _addTaskShowBottomSheet.asStateFlow()
    private var numberOfFetchProcess: Int = 0
    private var numberOfSignInProcess: Int = 0
    private var calendarYear: Int = 0
    private val myApplication = application as MyApplication
    private val _toastEvent = MutableSharedFlow<String>(replay = 0)
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        toggleIsViewLoading()
        userEmailUiState = appUserEmailId
        calendarYear = Calendar.getInstance().get(Calendar.YEAR);
        fetchUserDetails()
        fetchUserSignInStatus()
        fetchLimitedQuickLinks()
        fetchLimitedAnnouncements()
        getLeaveTrackerDetails()
        getHolidayDetails()
        fetchLimitedFavorites()
        if (!userEmailUiState.isNullOrBlank()) {
            appDataManager.listenForUserSignInStatusUpdates(userEmailUiState!!)
        }
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        if (userEmailUiState != null) {
            appDataManager.getFirebaseUser(userEmailUiState!!, ::updateUserDetails)
        }
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String) {
        numberOfFetchProcess--
        Log.d("MainScreenViewModel", "updateUserDetails called $userDetails")
        if ((response == "Success") && (userDetails != null)) {
            _liveUserDetails.value = userDetails
            myApplication.updateAppUserData(userDetails)
//            viewModelScope.launch {
//                appPreferenceDataStore.updateUserDetails(UserLoginData.from(userDetails))
//            }
        } else {
            //handle errors
            triggerToast("User details not found. Contact Sysadmin!")
        }
        if ((isViewLoading.value) && (numberOfFetchProcess == 0)) {
            toggleIsViewLoading()
        }
    }

    fun updateUserSignInStatus(location: Location?, context: Context) {
        if ((userEmailUiState != null) && ((userEmailUiState?.isNotBlank()) == true)) {
            if (isSignInViewLoading.value == false) toggleIsSignInViewLoading()
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
                        numberOfSignInProcess++
                        appDataManager.addSignInStatus(
                            userEmailUiState!!,
                            calendar.timeInMillis,
                            startOfTheDay.timeInMillis,
                            userLocation ?: "",
                            liveUserDetails.value.reportingTo.getValue("emailId"),
                            ::updateSignInResponse
                        )
                    }
                }
            } else {
                numberOfSignInProcess++
                appDataManager.addSignInStatus(
                    userEmailUiState!!,
                    calendar.timeInMillis,
                    startOfTheDay.timeInMillis,
                    "",
                    liveUserDetails.value.reportingTo.getValue("emailId"),
                    ::updateSignInResponse
                )
            }

        }
    }

    fun updateSignInResponse(response: String, error: String) {
        numberOfSignInProcess--
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateSignInResponse called $response")
        } else {
            //handle errors
            triggerToast("Unable to update USER signin status, try again.")
        }
        fetchUserSignInStatus()
        if ((isSignInViewLoading.value == true) && (numberOfSignInProcess == 0))
            toggleIsSignInViewLoading()
    }

    fun fetchUserSignInStatus() {
        val startOfTheDayInMillis = startOfTheDayInMillis(Calendar.getInstance().timeInMillis)
        if (!userEmailUiState.isNullOrBlank()) {
            if (!isSignInViewLoading.value) {
                toggleIsSignInViewLoading()
            }
            numberOfSignInProcess++
            appDataManager.getFirebaseAttendanceData(
                startOfTheDayInMillis,
                startOfTheDayInMillis + 86399990,
                userEmailUiState!!,
                ::updateAttendanceDetails
            )
        }
    }

    fun updateAttendanceDetails(attendanceData: QuerySnapshot?, response: String) {
        numberOfSignInProcess--
        if (response == "Success") {
            Log.d("UserInfoScreenViewModel", "updateAttendanceDetails called $attendanceData")
            val documentSnapshot = attendanceData?.first()
            if (documentSnapshot?.exists() == true) {
                _userAttendanceData.value = documentSnapshot.toObject(AttendanceData::class.java)
            }
        } else {
            //handle errors
            triggerToast("Unable to fetch USER signin status, try again.")
        }
        if ((isSignInViewLoading.value == true) && (numberOfSignInProcess == 0))
            toggleIsSignInViewLoading()
    }

    fun fetchLimitedQuickLinks() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getLimitedQuickLinks(3L, ::updateLimitedQuickLinksData)

    }

    fun updateLimitedQuickLinksData(quickLinksData: QuerySnapshot?, response: String) {
        numberOfFetchProcess--
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateQuickLinksData called $quickLinksData")
            quickLinksData?.count()?.let {
                if (it > 0) {
                    _quickLinksLimitedData.value = quickLinksData
                }
            }
        } else {
            //handle errors
            triggerToast("Unable to fetch quick links, try again.")
        }
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }

    fun fetchLimitedFavorites() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        if (userEmailUiState!=null){
            appDataManager.getFirebaseUserLimitedFavorites(3L, userEmailUiState!!,::updateLimitedFavouritesData)
        }
    }

    fun updateLimitedFavouritesData(favouritesData: QuerySnapshot?, response: String) {
        numberOfFetchProcess--
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateQuickLinksData called $favouritesData")
            favouritesData?.count()?.let {
                if (it > 0) {
                    _favouritesLimitedData.value = favouritesData
                } else {
                    _favouritesLimitedData.value = null
                }
            }
        } else {
            //handle errors
            triggerToast("Unable to fetch USER Favourites, try again.")
        }
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }


    fun fetchLimitedAnnouncements() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getLimitedAnnouncements(3L, ::updateLimitedAnnouncementsData)

    }

    fun updateLimitedAnnouncementsData(announcementData: QuerySnapshot?, response: String) {
        numberOfFetchProcess--
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateQuickLinksData called $announcementData")
            announcementData?.count()?.let {
                if (it > 0) {
                    _announcementsLimitedData.value = announcementData
                }
            }
        } else {
            //handle errors
            triggerToast("Unable to fetch USER Announcements, try again.")
        }
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }

    fun getLeaveTrackerDetails() {
        if ((userEmailUiState != null) && ((userEmailUiState?.isNotBlank()) == true)) {
            if (isViewLoading.value == false) toggleIsViewLoading()
            numberOfFetchProcess++
            appDataManager.getFirebaseLeaveTrackerData(
                calendarYear,
                userEmailUiState!!,
                ::updateLeaveTrackerData
            )
        }
    }

    fun updateLeaveTrackerData(leaveTrackerData: LeaveTrackerData?, response: String) {
        Log.d("MainScreenViewModel", "updateLeaveTrackerData called $leaveTrackerData")
        numberOfFetchProcess--
        if ((response == "Success") && (leaveTrackerData != null)) {
            _liveLeaveTrackerDetails.value = leaveTrackerData
        } else {
            //handle errors
            triggerToast("Unable to fetch USER leave details, try again.")
        }
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }

    fun getHolidayDetails() {
        if (isViewLoading.value == false) toggleIsViewLoading()
        numberOfFetchProcess++
        appDataManager.getHolidays(::updateHolidayData)
    }

    fun updateHolidayData(holidays: QuerySnapshot?, response: String) {
        Log.d("MainScreenViewModel", "updateHolidayData called $holidays")
        numberOfFetchProcess--
        if ((response == "Success") && (holidays != null)) {
            _holidaysData.value = holidays
        } else {
            //handle errors
            triggerToast("Unable to fetch Holidays, try again.")
        }
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }

    fun toggleAddTaskShowBottomSheet() {
        _addTaskShowBottomSheet.value = !_addTaskShowBottomSheet.value
    }

    fun toggleIsViewLoading() {
        _isViewLoading.value = !_isViewLoading.value
    }

    fun toggleIsSignInViewLoading() {
        _isSignInViewLoading.value = !_isSignInViewLoading.value
    }

    fun triggerToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    fun addUserToDB() {
//        for (i in 1..2) {
            appDataManager.addLeaveLogTemp(LeaveData(
                1,
                "Casual Leave",
                1.0f,
                1579737600000,
                1579737600000,
                "Approved",
                "ajay.kumar0495@gmail.com",
                "1001",
                "Ajay Kumar M",
                "team@gmail.com",
                1579478400000,
                "Casual Leave",
                "",0,0,
                0,0,0,0,
                "",
                "",
                2020
            ))
            appDataManager.addLeaveLogTemp(LeaveData(
                2,
                "Sick Leave",
                2.0f,
                1740268800000,
                1740355200000,
                "Approved",
                "ajay.kumar0495@gmail.com",
                "1001",
                "Ajay Kumar M",
                "team@gmail.com",
                1740009600000,
                "Sick Leave",
                "",0,0,
                0,0,0,0,
                "",
                "",
                2020
            ))
            appDataManager.addLeaveLogTemp(LeaveData(
                3,
                "On Duty",
                1.0f,
                1742688000000,
                1742688000000,
                "Approved",
                "ajay.kumar0495@gmail.com",
                "1001",
                "Ajay Kumar M",
                "team@gmail.com",
                1742428800000,
                "On Duty",
                "",0,0,
                0,0,0,0,
                "",
                "",
                2020
            ))
            appDataManager.addLeaveLogTemp(LeaveData(
                4,
                "Optional Holiday",
                1.0f,
                1745366400000,
                1745366400000,
                "Approved",
                "ajay.kumar0495@gmail.com",
                "1001",
                "Ajay Kumar M",
                "team@gmail.com",
                1745193600000,
                "Optional Holiday",
                "",0,0,
                0,0,0,0,
                "",
                "",
                2020
            ))
            appDataManager.addLeaveLogTemp(LeaveData(
                5,
                "Comp Off",
                1.0f,
                1748131200000,
                1748131200000,
                "Approved",
                "ajay.kumar0495@gmail.com",
                "1001",
                "Ajay Kumar M",
                "team@gmail.com",
                1747785600000,
                "Comp Off",
                "Full Day",
                0,0,
                10,30,18,30,
                "31 Dec 2020",
                "Days",
                2020
            ))


//        }


//        appDataManager.addNotificationDataTemp(NotificationData(
//            4,
//            "Ajay Kumar M has posted in Town Hall",
//            "Ajay Kumar M has posted in Town Hall",
//            Calendar.getInstance().timeInMillis,
//            "ajay.kumar0495@gmail.com",
//            "Ajay Kumar M",
//            "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
//            "Active",
//            0
//        ))
    }

    fun tempUpdateDb() {
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
                    "Department1",
                    mapOf()
                )
            )
        }
    }

    fun addAttendanceInitialData(){
        val startofthemonth = Calendar.getInstance()
        startofthemonth.apply {
            set(Calendar.MONTH,4)
            set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month
            set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
            set(Calendar.MINUTE, 0)       // Set minutes to 0
            set(Calendar.SECOND, 0)       // Set seconds to 0
            set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
        }
        var startDate = startofthemonth.timeInMillis
        val calendar = Calendar.getInstance()

        for(i in 1..31) {
            calendar.timeInMillis = startDate
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based
            val year = calendar.get(Calendar.YEAR)
            appDataManager.addAttendanceData(
                AttendanceData(
                    startDate,
                    0,
                    0,
                    "",
                    "",
                    "",
                    "",
                    liveUserDetails.value.email,
                    "",
                    0.0f,
                    day,
                    month,
                    year,
                    "",
                    liveUserDetails.value.reportingTo.getValue("emailId")
                )
            )
            startDate = startDate+86400000
        }
    }

    fun addGoalInitialData(){

        val startofthemonth = Calendar.getInstance()
        startofthemonth.apply {
            set(Calendar.MONTH,6)
            set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the month
            set(Calendar.HOUR_OF_DAY, 0)  // Set hour to midnight
            set(Calendar.MINUTE, 0)       // Set minutes to 0
            set(Calendar.SECOND, 0)       // Set seconds to 0
            set(Calendar.MILLISECOND, 0)  // Set milliseconds to 0
        }
        var startDate = startofthemonth.timeInMillis
        val calendar = Calendar.getInstance()

        for (i in 1..30) {
            appDataManager.addGoalData(
                GoalData(
                    "Project$i to be completed",
                    "$startDate",
                    "${startDate + 86400000}",
                    "Low",
                    "Project to be released before end of the year",
                    i,
                    "ajaym04021994@gmail.com",
                    mapOf(
                        Pair("1", "project comment1"),
                        Pair("2", "project comment2"),
                        Pair("3", "project comment3")
                    )
                ),
                i,
                true
            )
            startDate = startDate+604800000
        }
    }

    fun addLeaveTrackerInitialData() {
        for (i in 2026..2026) {
            appDataManager.addLeaveTrackerDataTemp(
                LeaveTrackerData(
                    "ajay.kumar0495@gmail.com",
                    i,
                    1001,
                    0,
                    8.0f, 4, 8.0f, 4, 8.0f, 4, 8.0f, 4, 8.0f, 4.0f,
                    mutableMapOf(),
                    "",
                    "Ajay Kumar M",
                    "teamMailId@gmail.com",
                    liveUserDetails.value.reportingTo.getValue("emailId")
                ),
                i
            )
        }
    }

    fun addAnnouncementListInitialData(){
        appDataManager.addAnnouncementTemp(
            AnnouncementList(
                1,
                "Announcement Title 1",
                Calendar.getInstance().timeInMillis,
                "General Awareness",
                1,
                3,
                "ajay.kumar0495@gmail.com",
                "Ajay Kumar M",
                "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",
                false,
                "Chennai"
            )
        )
    }

    fun addAnnouncementInitialData(){
        for (i in 1..5) {
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
                        Pair(
                            "1",
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
                                    Pair(
                                        "1",
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
                                    Pair(
                                        "1",
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
                                    Pair(
                                        "1",
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

    fun addDepartmentInitialData(){
        for (i in 1..5) {
            appDataManager.addDepartmentData(
                DepartmentInfo(
                    "Sub1Sub1Department${i}",
                    "5",
                    "Active",
                    mapOf(
                        Pair(
                            "Person1", mapOf(
                                Pair("Name", "Sub1Sub1Department${i}Person1"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "${i}1001"),
                                Pair("Email ID", "Sub1Sub1Department${i}Person1@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com"),
                                Pair("Department", "Sub1Sub1Department${i}")
                            )
                        ),
                        Pair(
                            "Person2", mapOf(
                                Pair("Name", "Sub1Sub1Department${i}Person2"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "${i}1001"),
                                Pair("Email ID", "Sub1Sub1Department${i}Person2@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com"),
                                Pair("Department", "Sub1Sub1Department${i}")
                            )
                        ),
                        Pair(
                            "Person3", mapOf(
                                Pair("Name", "Sub1Sub1Department${i}Person3"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "${i}1001"),
                                Pair("Email ID", "Sub1Sub1Department${i}Person3@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com"),
                                Pair("Department", "Sub1Sub1Department${i}")
                            )
                        ),
                        Pair(
                            "Person4", mapOf(
                                Pair("Name", "Sub1Sub1Department${i}Person4"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "${i}1001"),
                                Pair("Email ID", "Sub1Sub1Department${i}Person4@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com"),
                                Pair("Department", "Sub1Sub1Department${i}")
                            )
                        ),
                        Pair(
                            "Person5", mapOf(
                                Pair("Name", "Sub1Sub1Department${i}Person5"),
                                Pair("Designation", "Employee"),
                                Pair("Employee ID", "${i}1001"),
                                Pair("Email ID", "Sub1Sub1Department${i}Person5@company.com"),
                                Pair("Mobile Number", "1234"),
                                Pair(
                                    "Profile Image",
                                    "https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c"
                                ),
                                Pair("Profile URL", "www.google.com"),
                                Pair("Department", "Sub1Sub1Department${i}")
                            )
                        ),
                    )
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

*/