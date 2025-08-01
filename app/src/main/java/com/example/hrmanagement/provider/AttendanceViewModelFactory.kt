package com.example.hrmanagement.provider

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hrmanagement.data.UserSignInStatusRepository
import com.example.hrmanagement.ui.main.MainScreenViewModel
import com.example.hrmanagement.ui.userinfo.AttendanceViewModel

class AttendanceViewModelFactory (
    private val application: Application,
    private val userRepository: UserSignInStatusRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
                return AttendanceViewModel(application,userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }