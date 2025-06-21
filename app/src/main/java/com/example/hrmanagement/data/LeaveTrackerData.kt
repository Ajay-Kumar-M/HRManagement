package com.example.hrmanagement.data

import kotlinx.serialization.Serializable

@Serializable
data class LeaveTrackerData (
    val emailId: String = "",
    val yearValue: Int = 0,
    val employeeID: Int = 0,
    var lastLeaveId: Int = 0,
    var casualLeaveBalance: Float = 0.0f,
    val casualLeaveBooked: Int = 0,
    var sickLeaveBalance: Float = 0.0f,
    val sickLeaveBooked: Int = 0,
    var onDutyLeaveBalance: Float = 0.0f,
    val onDutyLeaveBooked: Int = 0,
    var optionalLeaveBalance: Float = 0.0f,
    val optionalLeaveBooked: Int = 0,
    var compOffLeaveBalance: Float = 0.0f,
    val compOffLeaveBooked: Float = 0.0f,
    val annualLeaveData: MutableMap<String,Map<String,String>> = mutableMapOf(),
    var status: String = "",
    val username: String = "",
    val teamEmailId: String = "",
    val reportingTo: String = ""
)

@Serializable
data class LeaveData(
    val leaveId: Int = 0,
    val leaveType: String = "",
    val numberOfDays: Float = 0.0f,
    val startDate: Long = 0,
    val endDate: Long = 0,
    var status: String = "",
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
    val otherInfo: Map<String,String> = mapOf(),
    val imageUrl: String = "",
    val comments: MutableMap<String,CommentsData> = mutableMapOf(),
    val isCommentsEnabled: Boolean = true,
    var lastCommentId: Int = 0,
    var commentsCount: Int = 0,
    val reportingTo: String = ""
)