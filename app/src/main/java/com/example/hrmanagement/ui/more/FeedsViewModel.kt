package com.example.hrmanagement.ui.more

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.Service.MyApplication.Companion.appUserEmailId
import com.example.hrmanagement.data.FeedData
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.UserLoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.TreeMap
import kotlin.collections.forEach

class FeedsViewModel: ViewModel() {

    var userLoginData = UserLoginData()
    private var _viewRecords: MutableStateFlow<TreeMap<Long,Any>> = MutableStateFlow(TreeMap<Long,Any>(compareByDescending { it }))
    val viewRecords = _viewRecords.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0
    val userEmail = appUserEmailId

    init {
        fetchUserDetails()
        fetchLeaveRequestsData()
        fetchFeedData()
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getFirebaseUser(userEmail, ::updateUserDetails)
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String) {
        numberOfFetchProcess--
        Log.d("FeedsViewModel", "updateUserDetails called $userDetails")
        if ((response == "Success") && (userDetails != null)) {
            userLoginData = userDetails
//            viewModelScope.launch {
//                appPreferenceDataStore.updateUserDetails(UserLoginData.from(userDetails))
//            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value) && (numberOfFetchProcess == 0)) {
            toggleIsViewLoading()
        }
    }

    fun fetchLeaveRequestsData(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.fetchLeaveLogs(2025, userEmail, 0) { querySnapshot, response, documentSnapshot ->
            Log.d("FeedsViewModel", "fetchLeaveRequestsData response called $response")
            if ((response == "Success")&&(querySnapshot!=null)) {
                querySnapshot.forEach { documentSnapshot ->
                    val tempData = documentSnapshot.toObject(LeaveData::class.java)
                    _viewRecords.value.put(tempData.dateOfRequest,tempData)
                }
            } else {
            }
            numberOfFetchProcess--
            if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
                toggleIsViewLoading()
        }
    }

    fun fetchFeedData(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getUserFeeds(userEmail) { querySnapshot, response ->
            Log.d("FeedsViewModel", "fetchFeedData response called $response")
            if ((response == "Success")&&(querySnapshot!=null)) {
                querySnapshot.forEach { documentSnapshot ->
                    val tempData = documentSnapshot.toObject(FeedData::class.java)
                    _viewRecords.value.put(tempData.timestamp,tempData)
                }
            } else {
            }
            numberOfFetchProcess--
            if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
                toggleIsViewLoading()
        }
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}