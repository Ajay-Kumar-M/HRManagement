package com.example.hrmanagement.data

import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable


data class UserSignInStatusData(
    var status: String? = "",
    var checkouttimestamp: Long = 0,
    var checkintimestamp: Long = 0
)