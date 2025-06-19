package com.example.hrmanagement.ui.requests

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.AttendanceRegularisationData
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.LeaveTrackerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class MyRequestsViewModel: ViewModel() {

    private var _leaveTrackerDocuments: List<LeaveTrackerData> = listOf()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0
    var userEmail: String?
    private var _pendingLeaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val pendingLeaveRequests = _pendingLeaveRequests.asStateFlow()
    private var _rejectedLeaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val rejectedLeaveRequests = _rejectedLeaveRequests.asStateFlow()
    private var _approvedLeaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val approvedLeaveRequests = _approvedLeaveRequests.asStateFlow()
    private var _attendanceRegularizationRequests: MutableStateFlow<List<AttendanceRegularisationData>> = MutableStateFlow(listOf())
    val attendanceRegularizationRequests = _attendanceRegularizationRequests.asStateFlow()

    init {
        Log.d("MyRequestsViewModel", "init")
        toggleIsViewLoading()
        runBlocking {
        userEmail = appPreferenceDataStore.emailFlow.firstOrNull()
        }
        fetchLeaveRequestsData()
        fetchAttendanceRegularizationData()
    }

    fun fetchLeaveRequestsData(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        if (userEmail != null) {
            appDataManager.fetchLeaveLogs(0,userEmail!!,0) { querySnapshot, response, documentSnapshot ->
                Log.d("MyRequestsViewModel", "response called $userEmail")
                if ((response == "Success")&&(querySnapshot!=null)) {
                    val tempPendingLeaveRequest: MutableList<LeaveData> = mutableListOf()
                    val tempApprovedLeaveRequest: MutableList<LeaveData> = mutableListOf()
                    val tempRejectedLeaveRequest: MutableList<LeaveData> = mutableListOf()
                    querySnapshot.forEach { leaveRequestDoc ->
                        leaveRequestDoc.toObject(LeaveData::class.java).let { leaveData ->
                            when(leaveData.status) {
                                "Approved" -> {
                                    tempApprovedLeaveRequest.add(leaveData)
                                }
                                "Pending" -> {
                                    tempPendingLeaveRequest.add(leaveData)
                                }
                                "Rejected" -> {
                                    tempRejectedLeaveRequest.add(leaveData)
                                }
                            }
                        }
                    }
                    _rejectedLeaveRequests.value = tempRejectedLeaveRequest
                    _pendingLeaveRequests.value = tempPendingLeaveRequest
                    _approvedLeaveRequests.value = tempApprovedLeaveRequest
                } else {

                }
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
                    toggleIsViewLoading()
            }
        }
    }

    fun fetchAttendanceRegularizationData(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        if (userEmail != null) {
            Log.d("MyRequestsViewModel", "fetchLeaveRequestsData called $userEmail")
            appDataManager.getAttendanceRegularizationData(userEmail!!) { querySnapshot, response ->
                Log.d("MyRequestsViewModel", "response called $userEmail")
                if ((response == "Success")&&(querySnapshot!=null)) {
                    _attendanceRegularizationRequests.value = querySnapshot.toObjects(
                        AttendanceRegularisationData::class.java)
                } else {

                }
                numberOfFetchProcess--
                if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
                    toggleIsViewLoading()
            }
        }
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}