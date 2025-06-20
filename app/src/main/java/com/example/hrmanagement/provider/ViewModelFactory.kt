package com.example.hrmanagement.provider

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.hrmanagement.ui.services.EmployeeDetailsViewModel
//
//class EmployeeDetailsViewModelFactory(
//    private val application: Application
//) : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(
//        modelClass: Class<T>,
//        extras: CreationExtras
//    ): T {
//        val savedStateHandle = extras.createSavedStateHandle()
//        return EmployeeDetailsViewModel(application, savedStateHandle) as T
//    }
//}


