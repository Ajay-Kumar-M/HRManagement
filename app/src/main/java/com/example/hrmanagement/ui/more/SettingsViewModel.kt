package com.example.hrmanagement.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.Service.MyApplication.Companion.appUserDetails
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {

    var userLoginData = appUserDetails

    fun logoutCurrentUser() {
        viewModelScope.launch {
            MyApplication.googleAuthenticationService.logout()
            appPreferenceDataStore.updateToken("")
        }
    }
}