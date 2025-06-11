package com.example.hrmanagement.ui.userinfo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ColleagueInfoScreenViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var _liveDepartmentDetails: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val liveDepartmentDetails = _liveDepartmentDetails.asStateFlow()
    var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFeatchProcess: Int = 0
    private var _liveColleagueDetails: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
    val liveColleagueDetails = _liveColleagueDetails.asStateFlow()
    var personEmailId: String = checkNotNull(savedStateHandle["personEmailId"])

    init {
        fetchUserDetails()
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        if(personEmailId.isNotBlank()) {
            appDataManager.getFirebaseUser(personEmailId,::updateUserDetails)
        }
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String){
        Log.d("MainScreenViewModel","updateUserDetails called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            _liveColleagueDetails.value = userDetails
//            viewModelScope.launch {
//                appPreferenceDataStore.updateUserDetails(UserLoginData.from(userDetails))
//            }
        } else {
            //handle errors
            TODO()
        }
        numberOfFeatchProcess--
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun getColleagueDepartmentMembers(departmentName: String){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.getFirebaseDepartment(departmentName,::updateDepartmentDetails)
    }

    fun updateDepartmentDetails(departmentInfoQuerySnapshot: QuerySnapshot?, response: String){
        Log.d("ColleagueInfoScreenViewModel","updateDepartmentDetails called ${departmentInfoQuerySnapshot?.size()}")
        if((response == "Success")&&(departmentInfoQuerySnapshot!=null)){
            _liveDepartmentDetails.value = departmentInfoQuerySnapshot
        } else {
            //handle errors
            TODO()
        }
        numberOfFeatchProcess--
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

}