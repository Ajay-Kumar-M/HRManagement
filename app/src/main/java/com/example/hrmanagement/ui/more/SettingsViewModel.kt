package com.example.hrmanagement.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.service.MyApplication
import com.example.hrmanagement.service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.service.MyApplication.Companion.appUserDetails
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