package com.example.hrmanagement.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.AppThemeMode
import kotlinx.coroutines.launch

class ThemeChangeViewModel: ViewModel() {

    fun onThemeChange(newMode: AppThemeMode) {
        viewModelScope.launch {
            appPreferenceDataStore.saveThemeMode(newMode)
        }
    }
}