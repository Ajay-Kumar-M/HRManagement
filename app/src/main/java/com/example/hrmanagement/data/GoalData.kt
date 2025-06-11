package com.example.hrmanagement.data

import kotlinx.serialization.Serializable

@Serializable
data class GoalData(
    val goalName: String = "",
    val startDate: String = "",
    val dueDate: String = "",
    val priority: String = "",
    val description: String = "",
    val progress: Int = 0,
    val emailId: String = "",
    val comments: Map<String,String> = mapOf()
)