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
    var status: String = ""
)