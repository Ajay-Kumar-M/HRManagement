package com.example.hrmanagement.ui.leave

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.LeaveTrackerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class LeaveReportViewModel(application: Application): AndroidViewModel(application) {

    private var calendarYear: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFeatchProcess: Int = 0
    private var _liveLeaveTrackerDetails: MutableStateFlow<LeaveTrackerData> = MutableStateFlow(LeaveTrackerData())
    val liveLeaveTrackerDetails = _liveLeaveTrackerDetails.asStateFlow()
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        calendarYear = Calendar.getInstance().get(Calendar.YEAR);
        getLeaveTrackerDetails()
    }

    fun getLeaveTrackerDetails(){
        if (appUserData.email.isNotBlank() == true) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFeatchProcess++
            appDataManager.getFirebaseLeaveTrackerData(calendarYear, appUserData.email,::updateLeaveTrackerData)
        }
    }

    fun updateLeaveTrackerData(leaveTrackerData: LeaveTrackerData?, response: String){
        Log.d("UserInfoScreenViewModel","updateLeaveTrackerData called $leaveTrackerData")
        numberOfFeatchProcess--
        if((response == "Success")&&(leaveTrackerData!=null)){
            _liveLeaveTrackerDetails.value = leaveTrackerData
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}