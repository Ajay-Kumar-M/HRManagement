package com.example.hrmanagement.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.NotificationData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationViewModel: ViewModel() {

    private var _notificationData: MutableStateFlow<List<NotificationData>> = MutableStateFlow(listOf())
    val notificationData = _notificationData.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFetchProcess: Int = 0

    init {
        fetchNotification()
    }

    fun fetchNotification(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getNotificationData(::updateNotificationData)
    }

    fun updateNotificationData(notificationData: QuerySnapshot?,response: String){
        if(response == "Success"){
            Log.d("NotificationViewModel","updateNotificationData called $notificationData")
            notificationData?.count()?.let {
                if(it > 0) {
                    _notificationData.value = notificationData.toObjects(NotificationData::class.java)
                }
            }
        } else {
            //handle errors
            TODO()
        }
        numberOfFetchProcess--
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}