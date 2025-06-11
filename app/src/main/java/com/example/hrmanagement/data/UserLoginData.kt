package com.example.hrmanagement.data

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginData(
    val provider: String = "",
    val token: String = "",
    val username: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val mobileNumber: String = "",
    var status: String = "",
    val emp_Id: String = "",
    val profileUrl: String = "",
    val departmentName: String = ""
){
    companion object {
        fun from(map: MutableMap<String, String>) = UserLoginData(
            provider = map["provider"].toString(),
            token = map["token"].toString(),
            username = map["username"].toString(),
            email = map["email"].toString(),
            imageUrl = map["imageUrl"].toString(),
            mobileNumber = map["mobileNumber"].toString(),
            status = map["status"].toString(),
            emp_Id = map["emp_Id"].toString(),
            profileUrl = map["profileUrl"].toString(),
            departmentName = map["departmentName"].toString()
        )
    }
}