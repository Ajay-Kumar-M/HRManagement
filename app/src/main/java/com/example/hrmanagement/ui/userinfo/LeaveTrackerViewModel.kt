package com.example.hrmanagement.ui.userinfo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.LeaveTrackerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class LeaveTrackerViewModel: ViewModel() {

    var numberOfFeatchProcess: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private val _leaveTrackerShowBottomSheet: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val leaveTrackerShowBottomSheet = _leaveTrackerShowBottomSheet.asStateFlow()
    private var _calendarYear: MutableStateFlow<Int> = MutableStateFlow(0)
    val calendarYear = _calendarYear.asStateFlow()
    var userEmailId: String? = ""
    private var _liveLeaveTrackerDetails: MutableStateFlow<LeaveTrackerData> = MutableStateFlow(LeaveTrackerData())
    val liveLeaveTrackerDetails = _liveLeaveTrackerDetails.asStateFlow()
    private var _leaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val leaveRequests = _leaveRequests.asStateFlow()
//    private val _showBottomSheetLeaveType: MutableStateFlow<String> = MutableStateFlow("")
    var showBottomSheetLeaveType = "" // _showBottomSheetLeaveType.asStateFlow()

    init {
        toggleIsViewLoading()
        runBlocking {
            userEmailId = appPreferenceDataStore.emailFlow.firstOrNull()
        }
        _calendarYear.value = Calendar.getInstance().get(Calendar.YEAR);
//        getLeaveTrackerDetails()
    }

    fun getLeaveTrackerDetails(){
        if ((userEmailId!=null)&&((userEmailId?.isNotBlank())==true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFeatchProcess++
            appDataManager.getFirebaseLeaveTrackerData(calendarYear.value, userEmailId!!,::updateLeaveTrackerData)
        }
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

    fun getLeaveRequests(){
        if (isViewLoading.value==false) toggleIsViewLoading()
        numberOfFeatchProcess++
        if (userEmailId!=null){
            appDataManager.fetchLeaveLogs(calendarYear.value,userEmailId!!,0) { querySnapshot, response, documentSnapshot ->
                if (response == "Success") {
                    _leaveRequests.value = querySnapshot?.toObjects(LeaveData::class.java) ?: listOf()
                } else {

                }
                numberOfFeatchProcess--
                if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
                    toggleIsViewLoading()
            }
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