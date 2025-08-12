package com.example.hrmanagement.ui.services

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.FavoritePerson
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ColleaguesViewModel(application: Application): AndroidViewModel(application) {

    var numberOfFetchProcess: Int = 0
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _favouritesData: MutableStateFlow<List<FavoritePerson>> = MutableStateFlow(listOf())
    val favouritesData = _favouritesData.asStateFlow()
    private var _isViewTypeList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewTypeList = _isViewTypeList.asStateFlow()
    private var _colleagueSearchText: MutableStateFlow<String> = MutableStateFlow("")
    val colleagueSearchText = _colleagueSearchText.asStateFlow()
    private var _allUsersData: MutableStateFlow<List<UserLoginData>> = MutableStateFlow(listOf(UserLoginData()))
    val allUsersData = _allUsersData.asStateFlow()
    private var _filteredUsersData: MutableStateFlow<List<UserLoginData>> = MutableStateFlow(listOf(UserLoginData()))
    val filteredUsersData = _filteredUsersData.asStateFlow()
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails
//    var personEmailId: String = checkNotNull(savedStateHandle["userEmailId"])

    init {
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getAllFirebaseUsers(::updateUserDetails)
    }

    fun updateUserDetails(userDetails: QuerySnapshot?, response: String){
        numberOfFetchProcess--
        if((response == "Success")&&(userDetails!=null)){
            Log.d("StatusViewModel","updateUserDetails called $userDetails")
            _allUsersData.value = userDetails.toObjects(UserLoginData::class.java)
//            _filteredUsersData.value.clear()
            _filteredUsersData.value = allUsersData.value
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun fetchFavorites() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getFirebaseUserFavorites(appUserData.email,::updateFavouritesData)

    }

    fun updateFavouritesData(favouritesData: QuerySnapshot?, response: String) {
        numberOfFetchProcess--
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateQuickLinksData called $favouritesData")
            favouritesData?.count()?.let {
                if (it > 0) {
                    _favouritesData.value = favouritesData.toObjects(FavoritePerson::class.java)
                }
            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
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