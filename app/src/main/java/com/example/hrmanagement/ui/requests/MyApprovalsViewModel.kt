package com.example.hrmanagement.ui.requests

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.AttendanceRegularisationData
import com.example.hrmanagement.data.LeaveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyApprovalsViewModel(application: Application): AndroidViewModel(application) {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0
    private var _pendingLeaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val pendingLeaveRequests = _pendingLeaveRequests.asStateFlow()
    private var _rejectedLeaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val rejectedLeaveRequests = _rejectedLeaveRequests.asStateFlow()
    private var _approvedLeaveRequests: MutableStateFlow<List<LeaveData>> = MutableStateFlow(listOf())
    val approvedLeaveRequests = _approvedLeaveRequests.asStateFlow()
    private var _pendingAttendanceRequests: MutableStateFlow<List<AttendanceRegularisationData>> = MutableStateFlow(listOf())
    val pendingAttendanceRequests = _pendingAttendanceRequests.asStateFlow()
    private var _rejectedAttendanceRequests: MutableStateFlow<List<AttendanceRegularisationData>> = MutableStateFlow(listOf())
    val rejectedAttendanceRequests = _rejectedAttendanceRequests.asStateFlow()
    private var _approvedAttendanceRequests: MutableStateFlow<List<AttendanceRegularisationData>> = MutableStateFlow(listOf())
    val approvedAttendanceRequests = _approvedAttendanceRequests.asStateFlow()
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        toggleIsViewLoading()
        fetchLeaveRequestsData()
        fetchAttendanceRegularizationData()
    }

    fun fetchLeaveRequestsData(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        if (appUserData.email.isNotBlank()) {
            appDataManager.fetchReportingToLeaveRecords(appUserData.email) { querySnapshot, response ->
                Log.d("MyApprovalsViewModel", "response called ${appUserData.email}")
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
        if (appUserData.email.isNotBlank()) {
            Log.d("MyRequestsViewModel", "fetchLeaveRequestsData called ${appUserData.email}")
            appDataManager.getReportingToAttendanceRegularizationData(appUserData.email) { querySnapshot, response ->
                Log.d("MyRequestsViewModel", "response called ${appUserData.email}")
                if ((response == "Success")&&(querySnapshot!=null)) {
                    val tempPendingAttendanceRequest: MutableList<AttendanceRegularisationData> = mutableListOf()
                    val tempApprovedAttendanceRequest: MutableList<AttendanceRegularisationData> = mutableListOf()
                    val tempRejectedAttendanceRequest: MutableList<AttendanceRegularisationData> = mutableListOf()
                    querySnapshot.forEach { attendanceRequestDoc ->
                        attendanceRequestDoc.toObject(AttendanceRegularisationData::class.java).let { attendanceData ->
                            when(attendanceData.status) {
                                "Approved" -> {
                                    tempApprovedAttendanceRequest.add(attendanceData)
                                }
                                "Pending" -> {
                                    tempPendingAttendanceRequest.add(attendanceData)
                                }
                                "Rejected" -> {
                                    tempRejectedAttendanceRequest.add(attendanceData)
                                }
                            }
                        }
                    }
                    _rejectedAttendanceRequests.value = tempRejectedAttendanceRequest
                    _pendingAttendanceRequests.value = tempPendingAttendanceRequest
                    _approvedAttendanceRequests.value = tempApprovedAttendanceRequest
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