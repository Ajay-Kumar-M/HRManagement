package com.example.hrmanagement.data

import kotlinx.serialization.Serializable

@Serializable
data class LeaveTrackerData (
    val emailId: String = "",
    val yearValue: Int = 0,
    val employeeID: Int = 0,
    var lastLeaveId: Int = 0,
    var casualLeaveBalance: Int = 0,
    val casualLeaveBooked: Int = 0,
    var sickLeaveBalance: Int = 0,
    val sickLeaveBooked: Int = 0,
    var onDutyLeaveBalance: Int = 0,
    val onDutyLeaveBooked: Int = 0,
    var optionalLeaveBalance: Int = 0,
    val optionalLeaveBooked: Int = 0,
    var compOffLeaveBalance: Int = 0,
    val compOffLeaveBooked: Int = 0,
    val annualLeaveData: MutableMap<String,Map<String,String>> = mutableMapOf(),
    var status: String = "",
    val username: String = "",
    val teamEmailId: String = ""
)

@Serializable
data class LeaveData(
    val leaveId: Int = 0,
    val leaveType: String = "",
    val numberOfDays: Int = 0,
    val startDate: Long = 0,
    val endDate: Long = 0,
    val status: String = "",
    val emailId: String = "",
    val employeeId: String = "",
    val employeeName: String = "",
    val teamMailId: String = "",
    val dateOfRequest: Long = 0,
    val reasonForLeave: String = "",
    val duration: String = "",
    val durationHour: Int = 0,
    val durationMinute: Int = 0,
    val startTimeHour: Int = 0,
    val startTimeMinute: Int = 0,
    val endTimeHour: Int = 0,
    val endTimeMinute: Int = 0,
    val expiry: String = "",
    val unit: String = "",
    val year: Int = 0,
    val fromDateString: String = "",
    val toDateString: String = "",
    val dateOfRequestString: String = "",
    val otherInfo: Map<String,String> = mapOf()
)