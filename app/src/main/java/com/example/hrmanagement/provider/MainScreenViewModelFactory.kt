package com.example.hrmanagement.provider

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hrmanagement.data.UserSignInStatusRepository
import com.example.hrmanagement.ui.main.MainScreenViewModel

class MainScreenViewModelFactory(
    private val application: Application,
    private val userRepository: UserSignInStatusRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            return MainScreenViewModel(application,userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
