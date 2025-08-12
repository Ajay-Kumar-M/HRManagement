package com.example.hrmanagement.ui.leave

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.AttendanceRegularisationData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaveRegularisationDetailsViewModel: ViewModel() {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFetchProcess: Int = 0
    private val _popBackStackEvent = MutableSharedFlow<Unit>()
    val popBackStackEvent = _popBackStackEvent.asSharedFlow()

    fun approveRegularizationRequest(attendanceData: AttendanceRegularisationData){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        val tempAttendanceData = attendanceData
        tempAttendanceData.status = "Approved"
        tempAttendanceData.checkInTime = tempAttendanceData.regularisedCheckInTime
        tempAttendanceData.checkOutTime = tempAttendanceData.regularisedCheckOutTime
        tempAttendanceData.totalHours = tempAttendanceData.regularisedTotalHours
        appDataManager.removeAndAddAttendanceRegularizationData(attendanceData) { response ->
            if (response == "Success") {
                Log.d("LeaveRegularisationDetailsViewModel","approveRegularizationRequest reject record updated $response")
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                    toggleIsViewLoading()
                }
                viewModelScope.launch {
                    _popBackStackEvent.emit(Unit)
                }
            } else {
                Log.d("LeaveRegularisationDetailsViewModel","approveRegularizationRequest record not updated $response")
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                    toggleIsViewLoading()
                }
            }
        }
    }

    fun rejectRegularizationRequest(attendanceData: AttendanceRegularisationData){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        val tempAttendanceData = attendanceData
        tempAttendanceData.status = "Rejected"
        appDataManager.addRegularisationAttendanceData(attendanceData) { response ->
            if (response == "Success") {
                Log.d("LeaveRegularisationDetailsViewModel","rejectRegularizationRequest reject record updated $response")
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0)) {
                    toggleIsViewLoading()
                }
                viewModelScope.launch {
                    _popBackStackEvent.emit(Unit)
                }
            } else {
                Log.d("LeaveRegularisationDetailsViewModel","rejectRegularizationRequest record not updated $response")
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