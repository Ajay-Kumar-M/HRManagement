package com.example.hrmanagement.ui.more

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.AppThemeMode
import kotlinx.coroutines.launch

class ThemeChangeViewModel: ViewModel() {

    fun onThemeChange(newMode: AppThemeMode) {
        viewModelScope.launch {
            appPreferenceDataStore.saveThemeMode(newMode)
        }
    }
}