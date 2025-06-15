package com.example.hrmanagement.ui.services

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.UserLoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmployeeDetailsViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var numberOfFeatchProcess: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _liveUserDetails: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
    val liveUserDetails = _liveUserDetails.asStateFlow()
    var emailId: String = checkNotNull(savedStateHandle["userEmailId"])

    init {
        toggleIsViewLoading()
        fetchUserDetails()
        toggleIsViewLoading()
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        if(true) {
            appDataManager.getFirebaseUser(emailId,::updateUserDetails)
        }
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String){
        numberOfFeatchProcess--
        Log.d("MainScreenViewModel","updateUserDetails called $userDetails")
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