package com.example.hrmanagement.ui.userinfo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GoalsViewModel(application: Application): AndroidViewModel(application) {


    private var _goalsQuerySnapshot: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val goalsQuerySnapshot = _goalsQuerySnapshot.asStateFlow()
    var numberOfFetchProcess: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        toggleIsViewLoading()
        getGoals()
    }

    fun getGoals(){
        if (appUserData.email.isNotBlank() == true) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFetchProcess++
            appDataManager.getUserGoalsData(appUserData.email,::updateUserGoalsData)
        }
    }

    fun updateUserGoalsData(goalsData: QuerySnapshot?, status: String){
        Log.d("UserInfoScreenViewModel","updateLeaveTrackerData called $goalsData")
        numberOfFetchProcess--
        if(status == "Success"){
//            _liveLeaveTrackerDetails.value = leaveTrackerData
            _goalsQuerySnapshot.value = goalsData
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}