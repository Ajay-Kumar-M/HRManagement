package com.example.hrmanagement.ui.services

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ColleaguesViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var numberOfFeatchProcess: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _isViewTypeList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewTypeList = _isViewTypeList.asStateFlow()
    private var _colleagueSearchText: MutableStateFlow<String> = MutableStateFlow("")
    val colleagueSearchText = _colleagueSearchText.asStateFlow()
    private var _allUsersData: MutableStateFlow<List<UserLoginData>> = MutableStateFlow(listOf(UserLoginData()))
    val allUsersData = _allUsersData.asStateFlow()
    private var _filteredUsersData: MutableStateFlow<List<UserLoginData>> = MutableStateFlow(listOf(UserLoginData()))
    val filteredUsersData = _filteredUsersData.asStateFlow()
//    var personEmailId: String = checkNotNull(savedStateHandle["userEmailId"])

    init {
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.getAllFirebaseUsers(::updateUserDetails)
    }

    fun updateUserDetails(userDetails: QuerySnapshot?, response: String){
        numberOfFeatchProcess--
        if((response == "Success")&&(userDetails!=null)){
            Log.d("StatusViewModel","updateUserDetails called $userDetails")
            _allUsersData.value = userDetails.toObjects(UserLoginData::class.java)
//            _filteredUsersData.value.clear()
            _filteredUsersData.value = allUsersData.value
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun colleagueSearchTextChanged(searchText: String){
        toggleIsViewLoading()
        _colleagueSearchText.value = searchText
//        _filteredUsersData.value.clear()
        _filteredUsersData.value = allUsersData.value.filter {
                it.username.contains(searchText,true) || it.emp_Id.contains(searchText,true) || it.email.contains(searchText,true)
            }
        toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun toggleIsViewType(){
        _isViewTypeList.value = !_isViewTypeList.value
    }
}