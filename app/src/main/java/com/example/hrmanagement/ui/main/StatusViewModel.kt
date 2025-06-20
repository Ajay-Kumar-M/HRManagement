package com.example.hrmanagement.ui.main

import android.app.Application
import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.FeedData
import com.example.hrmanagement.data.FeedMetadata
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class StatusViewModel(application: Application): AndroidViewModel(application) {

    private var _isSearchDropdownExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSearchDropdownExpanded = _isSearchDropdownExpanded.asStateFlow()
    private var _isSuccessDialogVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSuccessDialogVisible = _isSuccessDialogVisible.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var allUsersData: List<UserLoginData> = listOf()
    private var _filteredUsersData: MutableStateFlow<MutableList<UserLoginData>> = MutableStateFlow(mutableListOf(UserLoginData()))
    val filteredUsersData = _filteredUsersData.asStateFlow()
    private var _statusData: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue(""))
    val statusData = _statusData.asStateFlow()
    private var _dollarEmailMapData = mutableMapOf<Int,String>()
    private var _dollarMapData: MutableStateFlow<MutableMap<Int,String>> = MutableStateFlow(mutableMapOf())
    val dollarMapData = _dollarMapData.asStateFlow()
    private var numberOfFetchProcess: Int = 0
    private var searchStringData: String = ""
    private val myApplication = application as MyApplication
    val userData = myApplication.appUserDetails

    init {
        fetchAllUsers()
    }

    fun addUserStatus(emailId: String) {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getUserLastFeedData(emailId,::updateFeedMetadata)
    }

    fun updateFeedMetadata(feedMetadata: FeedMetadata?, response: String){
        if((response == "Success")&&(feedMetadata!=null)){
            Log.d("StatusViewModel","updateUserDetails called $feedMetadata")
            val builder = StringBuilder()
            var current = 0
            val sortedReplacements = _dollarEmailMapData.toSortedMap()
            for ((index, replacement) in sortedReplacements) {
                builder.append(statusData.value.text.substring(current, index))
                builder.append(replacement)
                current = index + 1
            }
            if (current < statusData.value.text.length) {
                builder.append(statusData.value.text.substring(current))
            }
            val result = builder.toString()
//            var stringWithEmail = statusData.value.text
//            _dollarEmailMapData.entries.sortedByDescending { it.key }.forEach { (index, email) ->
//                stringWithEmail = stringWithEmail.replaceRange(index, index + 1, email)
//            }
            val feedData = FeedData(
                (feedMetadata.lastFeedId+1).toString(),
                feedMetadata.username,
                feedMetadata.email,
                feedMetadata.imageUrl,
                "You have posted a message",
                Calendar.getInstance().timeInMillis,
                "userstatus",
                builder.toString(),
                true,
                true,
                0,0,0,0,
                mutableMapOf(),
                mutableMapOf(),
                mapOf()
            )
            appDataManager.addUserStatusData(feedData,true,feedMetadata.feedCount+1,::updateStatusResponse)
        } else {
            //handle errors
            numberOfFetchProcess--
            if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
                toggleIsViewLoading()
            }
        }
    }

    fun updateStatusResponse(response: String){
        numberOfFetchProcess--
        if(response == "Success"){
            Log.d("StatusViewModel","updateStatusResponse called $response")
            _statusData.value = TextFieldValue("")
            toggleSuccessDialog()
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
            toggleIsViewLoading()
        }
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
            allUsersData = userDetails.toObjects(UserLoginData::class.java)
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun filterUsers(searchString: String){
        _filteredUsersData.value.clear()
        searchStringData = searchString
        _filteredUsersData.value.addAll(allUsersData.filter {
            it.username.contains(searchString, ignoreCase = true)
        })
        if (filteredUsersData.value.isNotEmpty()){
            Log.d("StatusViewModel","filterUsers 2 ${filteredUsersData.value}")
            if (!_isSearchDropdownExpanded.value){
                toggleSearchDropdown()
            }
        } else if (_isSearchDropdownExpanded.value) {
            toggleSearchDropdown()
        }
    }

    fun clearFilteredUserData(){
        _filteredUsersData.value.clear()
    }

    fun onMentionSelection(user: UserLoginData){
        // Example: Replace "@old" with "@new"
        val oldText = statusData.value.text
        addDollarMap(oldText.indexOf("@${searchStringData}"), user.username, user.email)
        val newText = oldText.replace("@${searchStringData}", "$") // Replace as needed
        val newSelection = TextRange(newText.length) // Or set cursor as needed

        val newValue = statusData.value.copy(
            text = newText,
            selection = newSelection
        )
        onStatusChange(newValue)
    }

    fun removeDollarMap(index: Int) {
        _dollarMapData.value.remove(index)
        _dollarEmailMapData.remove(index)
    }

    fun addDollarMap(index: Int, username: String, email: String) {
        _dollarMapData.value.put(index,username)
        _dollarEmailMapData.put(index, email)
    }

    fun onStatusChange(newText: TextFieldValue) {
        _statusData.value = newText
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun toggleSuccessDialog(){
        _isSuccessDialogVisible.value = !_isSuccessDialogVisible.value
    }

    fun toggleSearchDropdown(){
        _isSearchDropdownExpanded.value = !_isSearchDropdownExpanded.value
    }
}