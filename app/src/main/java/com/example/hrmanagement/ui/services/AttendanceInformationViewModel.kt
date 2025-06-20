package com.example.hrmanagement.ui.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication

class AttendanceInformationViewModel(application: Application): AndroidViewModel(application) {

    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails
}