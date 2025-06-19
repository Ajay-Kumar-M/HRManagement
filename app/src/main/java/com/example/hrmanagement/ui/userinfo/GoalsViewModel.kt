package com.example.hrmanagement.ui.userinfo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.service.MyApplication.Companion.appPreferenceDataStore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class GoalsViewModel: ViewModel() {


    private var _goalsQuerySnapshot: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val goalsQuerySnapshot = _goalsQuerySnapshot.asStateFlow()
    var numberOfFeatchProcess: Int = 0
    var userEmailId: String? = ""
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()

    init {
        toggleIsViewLoading()
        runBlocking {
            userEmailId = appPreferenceDataStore.emailFlow.firstOrNull()
        }
        getGoals()
    }

    fun getGoals(){
        if ((userEmailId!=null)&&((userEmailId?.isNotBlank())==true)) {
            if (isViewLoading.value==false) toggleIsViewLoading()
            numberOfFeatchProcess++
            appDataManager.getUserGoalsData(userEmailId!!,::updateUserGoalsData)
        }
    }

    fun updateUserGoalsData(goalsData: QuerySnapshot?, status: String){
        Log.d("UserInfoScreenViewModel","updateLeaveTrackerData called $goalsData")
        numberOfFeatchProcess--
        if(status == "Success"){
//            _liveLeaveTrackerDetails.value = leaveTrackerData
            _goalsQuerySnapshot.value = goalsData
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