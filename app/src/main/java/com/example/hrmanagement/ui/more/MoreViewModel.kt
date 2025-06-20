package com.example.hrmanagement.ui.more

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MoreViewModel(application: Application): AndroidViewModel(application) {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

}