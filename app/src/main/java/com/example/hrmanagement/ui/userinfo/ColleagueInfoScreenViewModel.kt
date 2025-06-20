package com.example.hrmanagement.ui.userinfo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.FavoritePerson
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ColleagueInfoScreenViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var _liveDepartmentDetails: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val liveDepartmentDetails = _liveDepartmentDetails.asStateFlow()
    var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFeatchProcess: Int = 0
    private var _liveUserFavoriteDetails: MutableStateFlow<List<FavoritePerson>> = MutableStateFlow(listOf())
    val liveUserFavoriteDetails = _liveUserFavoriteDetails.asStateFlow()
    private var _liveColleagueDetails: MutableStateFlow<UserLoginData> = MutableStateFlow(UserLoginData())
    val liveColleagueDetails = _liveColleagueDetails.asStateFlow()
    var colleagueEmailId: String = checkNotNull(savedStateHandle["colleagueEmailId"])
    var myEmailId: String = checkNotNull(savedStateHandle["myEmailId"])
    private val _toastEvent = MutableSharedFlow<String>(replay = 0)
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        fetchColleagueDetails()
        fetchUserFavoritesDetails()
    }

    fun fetchUserFavoritesDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        if(colleagueEmailId.isNotBlank()) {
            appDataManager.getFirebaseUserFavorites(myEmailId,::updateUserDetails)
        }
    }

    fun updateUserDetails(userFavoriteDetails: QuerySnapshot?, response: String){
        Log.d("ColleagueInfoScreenViewModel","updateUserDetails called $userFavoriteDetails")
        if((response == "Success")&&(userFavoriteDetails!=null)){
            _liveUserFavoriteDetails.value = userFavoriteDetails.toObjects(FavoritePerson::class.java)
        } else {
            //handle errors
            TODO()
            triggerToast("User Favorites not found")
        }
        numberOfFeatchProcess--
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun fetchColleagueDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        if(colleagueEmailId.isNotBlank()) {
            appDataManager.getFirebaseUser(colleagueEmailId,::updateColleagueDetails)
        }
    }

    fun updateColleagueDetails(userDetails: UserLoginData?, response: String){
        Log.d("ColleagueInfoScreenViewModel","updateUserDetails called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            _liveColleagueDetails.value = userDetails
        } else {
            //handle errors
            TODO()
            triggerToast("Unable to fetch Colleague Details. Try again!")
        }
        numberOfFeatchProcess--
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun addColleagueToFavorites(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        if(colleagueEmailId.isNotBlank()) {
            appDataManager.addUserFavorite(FavoritePerson(
                liveColleagueDetails.value.username,
                liveColleagueDetails.value.email,
                liveColleagueDetails.value.imageUrl,
                liveColleagueDetails.value.emp_Id,
                myEmailId
            ),::addFavoriteResponse)
        }
    }

    fun addFavoriteResponse(response: String){
        if(response == "Success"){
            Log.d("ColleagueInfoScreenViewModel","addFavoriteResponse called $response")
            val tempFavorites = liveUserFavoriteDetails.value.toMutableList()
            tempFavorites.add(FavoritePerson(
                liveColleagueDetails.value.username,
                liveColleagueDetails.value.email,
                liveColleagueDetails.value.imageUrl,
                liveColleagueDetails.value.emp_Id,
                myEmailId
            )
            )
            _liveUserFavoriteDetails.value = tempFavorites
            triggerToast("Added to Favorites")
        } else {
            //handle errors
            TODO()
            triggerToast("Not able to add to Favorites, Try again !")
        }
        numberOfFeatchProcess--
        if((isViewLoading.value)&&(numberOfFeatchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun removeColleagueFromFavorites(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.removeUserFavorite(myEmailId,colleagueEmailId,::removeFavoriteResponse)
    }

    fun removeFavoriteResponse(response: String){
        if(response == "Success"){
            Log.d("ColleagueInfoScreenViewModel","addFavoriteResponse called $response")
            val tempFavorites = liveUserFavoriteDetails.value.toMutableList()
            tempFavorites.removeIf { it.email == colleagueEmailId }
            _liveUserFavoriteDetails.value = tempFavorites
            triggerToast("Removed from Favorites")
        } else {
            //handle errors
            TODO()
            triggerToast("Not able to remove from Favorites, Try again !")
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
            triggerToast("Unable to fetch Department Details")
        }
        numberOfFeatchProcess--
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun triggerToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }
}