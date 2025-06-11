package com.example.hrmanagement.ui.announcement

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnnouncementsFilterViewModel(): ViewModel() {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var _categoryChildCheckedStates: MutableStateFlow<MutableMap<String,Boolean>> = MutableStateFlow(
        mutableStateMapOf(
            Pair("General Awareness",false),
            Pair("Bank Related",false),
            Pair("Corporate Gifts",false),
            Pair("Insurance",false),
            Pair("ID Card",false)
        )
    )
    val categoryChildCheckedStates = _categoryChildCheckedStates.asStateFlow()
    private var _locationChildCheckedStates: MutableStateFlow<MutableMap<String,Boolean>> = MutableStateFlow(
        mutableStateMapOf(
            Pair("Chennai",false),
            Pair("Bangalore",false),
        )
    )
    val locationChildCheckedStates = _locationChildCheckedStates.asStateFlow()

    fun checkCategoryChildBox(index: String, newState: Boolean) {
        _categoryChildCheckedStates.value[index] = newState

    }

    fun checkLocationChildBox(index: String, newState: Boolean) {
        _locationChildCheckedStates.value[index] = newState
    }

    fun resetAllCheckBox() {
        for (key in _categoryChildCheckedStates.value.keys.toList()) {
            _categoryChildCheckedStates.value[key] = false
        }
        for (key in _locationChildCheckedStates.value.keys.toList()) {
            _locationChildCheckedStates.value[key] = false
        }
    }

    fun toggleViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

}