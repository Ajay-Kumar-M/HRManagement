package com.example.hrmanagement.data

data class NotificationData(
    val notificationId: Int = 0,
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val reporterEmailId: String = "",
    val reporterName: String = "",
    val reporterProfileImageUrl: String = "",
    val status: String = "",
    val expiry: Long = 0L
)