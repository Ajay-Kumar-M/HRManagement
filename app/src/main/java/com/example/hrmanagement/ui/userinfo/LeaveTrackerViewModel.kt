package com.example.hrmanagement.ui.userinfo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.LeaveTrackerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class LeaveTrackerViewModel(application: Application): AndroidViewModel(application) {

    var numberOfFetchProcess: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private val _leaveTrackerShowBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val leaveTrackerShowBottomSheet = _leaveTrackerShowBottomSheet.asStateFlow()
    private var _calendarYear: MutableStateFlow<Int> = MutableStateFlow(0)
    val calendarYear = _calendarYear.asStateFlow()
    private var _liveLeaveTrackerDetails: MutableStateFlow<LeaveTrackerData> = MutableStateFlow(LeaveTrackerData())
    val liveLeaveTrackerDetails = _liveLeaveTrackerDetails.asStateFlow()
    private var _leaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val leaveRequests = _leaveRequests.asStateFlow()
//    private val _showBottomSheetLeaveType: MutableStateFlow<String> = MutableStateFlow("")
    var showBottomSheetLeaveType = "" // _showBottomSheetLeaveType.asStateFlow()
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        toggleIsViewLoading()
        _calendarYear.value = Calendar.getInstance().get(Calendar.YEAR);
//        getLeaveTrackerDetails()
    }

    fun getLeaveTrackerDetails(){
        if (appUserData.email.isNotBlank() == true) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFetchProcess++
            appDataManager.getFirebaseLeaveTrackerData(calendarYear.value, appUserData.email,::updateLeaveTrackerData)
        }
    }

    fun updateLeaveTrackerData(leaveTrackerData: LeaveTrackerData?, response: String){
        Log.d("UserInfoScreenViewModel","updateLeaveTrackerData called $leaveTrackerData")
        numberOfFetchProcess--
        if((response == "Success")&&(leaveTrackerData!=null)){
            _liveLeaveTrackerDetails.value = leaveTrackerData
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
    }

    fun getLeaveRequests(){
        if (isViewLoading.value==false) toggleIsViewLoading()
        numberOfFetchProcess++
            appDataManager.fetchLeaveLogs(calendarYear.value,appUserData.email,0) { querySnapshot, response, documentSnapshot ->
                if (response == "Success") {
                    _leaveRequests.value = querySnapshot?.toObjects(LeaveData::class.java) ?: listOf()
                } else {

                }
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
                    toggleIsViewLoading()
            }
    }

    fun changeShowBottomSheetLeaveType(leaveType: String){
        showBottomSheetLeaveType = leaveType
        toggleLeaveTrackerShowBottomSheet()
    }

    fun toggleLeaveTrackerShowBottomSheet(){
        _leaveTrackerShowBottomSheet.value = !_leaveTrackerShowBottomSheet.value
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
}