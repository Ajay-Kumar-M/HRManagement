package com.example.hrmanagement.ui.more

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication

class SettingsViewModel(application: Application): AndroidViewModel(application) {

    private val myApplication = application as MyApplication
    val userLoginData = myApplication.appUserDetails

    fun logoutCurrentUser(context: Context) {
    }
}