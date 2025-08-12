package com.example.hrmanagement.provider

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.hrmanagement.data.UserSignInStatusRepository
import com.example.hrmanagement.ui.userinfo.UserInfoScreenViewModel

class UserInfoScreenViewModelFactory (
//    private val application: Application,
    private val userRepository: UserSignInStatusRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val applicationExtras = checkNotNull(extras[APPLICATION_KEY])
            if (modelClass.isAssignableFrom(UserInfoScreenViewModel::class.java)) {
                return UserInfoScreenViewModel(applicationExtras,userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }