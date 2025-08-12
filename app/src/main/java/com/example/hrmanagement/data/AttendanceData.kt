package com.example.hrmanagement.data

import kotlinx.serialization.Serializable

data class AttendanceData(
    val date: Long = 0L,
    var checkInTime: Long = 0L,
    var checkOutTime: Long = 0L,
    var checkInLocation: String = "",
    var checkOutLocation: String = "",
    val checkInNote: String = "",
    val checkOutNote: String = "",
    val emailId: String = "",
    var status: String = "",
    var totalHours: Float = 0.0f,
    val friendlyDateValue: Int = 0,
    val friendlyMonthValue: Int = 0,
    val friendlyYearValue: Int = 0,
    var leaveType: String = "",
    val reportingTo: String = ""
)

@Serializable
data class AttendanceRegularisationData(
    val date: Long = 0L,
    var checkInTime: Long = 0L,
    var checkOutTime: Long = 0L,
    var checkInLocation: String = "",
    var checkOutLocation: String = "",
    val checkInNote: String = "",
    val checkOutNote: String = "",
    val emailId: String = "",
    var status: String = "Pending",
    var totalHours: Float = 0.0f,
    val friendlyDateValue: Int = 0,
    val friendlyMonthValue: Int = 0,
    val friendlyYearValue: Int = 0,
    var leaveType: String = "",
    var regularisedCheckInTime: Long = 0L,
    var regularisedCheckOutTime: Long = 0L,
    var regularisedTotalHours: Float = 0.0f,
    var regularisedDescription: String = "",
    val reportingTo: String = ""
)