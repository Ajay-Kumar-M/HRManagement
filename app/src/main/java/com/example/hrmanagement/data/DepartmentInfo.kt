package com.example.hrmanagement.data

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentInfo(
    val name: String = "",
    val membersCount: String = "",
    var status: String = "",
    val membersInfo: Map<String, Map<String,String>> = mapOf()
)