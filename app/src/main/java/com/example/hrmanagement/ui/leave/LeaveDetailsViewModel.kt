package com.example.hrmanagement.ui.leave

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.LeaveData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaveDetailsViewModel(): ViewModel() {

//    private val myApplication = application as MyApplication
//    val appUserData = myApplication.appUserDetails
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFetchProcess: Int = 0

    private val _popBackStackEvent = MutableSharedFlow<Unit>()
    val popBackStackEvent = _popBackStackEvent.asSharedFlow()

    fun approveLeave(leaveData: LeaveData){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        leaveData.status = "Approved"
        if (leaveData.leaveType=="Comp Off"){
            appDataManager.getFirebaseLeaveTrackerData(leaveData.year, leaveData.emailId) { leaveTrackerData, response ->
                if (response=="Success") {
                    val responseLeaveTrackerData = leaveTrackerData
                    responseLeaveTrackerData.compOffLeaveBalance = responseLeaveTrackerData.compOffLeaveBalance+leaveData.numberOfDays
                    appDataManager.addLeaveTrackerData(responseLeaveTrackerData, leaveData.year, leaveData) { addRecordResponse ->
                        if (addRecordResponse == "Success") {
                            Log.d("LeaveDetailsViewModel","approveLeave record updated $addRecordResponse")
                            numberOfFetchProcess--
                            if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                                toggleIsViewLoading()
                            }
                            viewModelScope.launch {
                                _popBackStackEvent.emit(Unit)
                            }
                        } else {
                            //not able to update leaveData and leaveTrackerData
                            numberOfFetchProcess--
                            if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                                toggleIsViewLoading()
                            }
                        }
                    }
                } else{
                    //leaveTrackerData not found
                    numberOfFetchProcess--
                    if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                        toggleIsViewLoading()
                    }
                }
            }
        } else {
            appDataManager.addLeaveData(leaveData){ response ->
                if (response=="Success") {
                    Log.d("LeaveDetailsViewModel","approve record updated $response")
                    numberOfFetchProcess--
                    if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                        toggleIsViewLoading()
                    }
                    viewModelScope.launch {
                        _popBackStackEvent.emit(Unit)
                    }
                } else {
                    //not able to update leaveData
                    Log.d("LeaveDetailsViewModel","approve record updated $response")
                    numberOfFetchProcess--
                    if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                        toggleIsViewLoading()
                    }
                }
            }
        }
    }

    fun rejectLeave(leaveData: LeaveData){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        leaveData.status = "Rejected"
        appDataManager.addLeaveData(leaveData){ response ->
            if (response=="Success") {
                Log.d("LeaveDetailsViewModel","rejectLeave record updated $response")
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                    toggleIsViewLoading()
                }
                viewModelScope.launch {
                    _popBackStackEvent.emit(Unit)
                }
            } else {
                //not able to update leaveData
                Log.d("LeaveDetailsViewModel","rejectLeave record updated $response")
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                    toggleIsViewLoading()
                }
            }
        }
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

}