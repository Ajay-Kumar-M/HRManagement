package com.example.hrmanagement.data

data class MoreItemData(
    val id: Int,
    val title: String,
    val icon: Int,
    val navigationLink: String,
    val subItems: Map<String,String>
)