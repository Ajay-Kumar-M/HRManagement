package com.example.hrmanagement.data

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuth(
    val provider: String = "",
    val token: String = "",
    val username: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val mobileNumber: String = "",
    val status: String = "",
    val profileUrl: String = "",
    val departmentName: String = "",
    val reportingTo: Map<String,String> = mapOf()
)