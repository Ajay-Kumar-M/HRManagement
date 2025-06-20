package com.example.hrmanagement.ui.services

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.UserLoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmployeeDetailsViewModel(application: Application): AndroidViewModel(application) {

    var numberOfFeatchProcess: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _liveUserDetails: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
    val liveUserDetails = _liveUserDetails.asStateFlow()
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        fetchUserDetails()
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        if(true) {
            appDataManager.getFirebaseUser(appUserData.email,::updateUserDetails)
        }
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String){
        numberOfFeatchProcess--
        Log.d("EmployeeDetailsViewModel","updateUserDetails called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            _liveUserDetails.value = userDetails
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}