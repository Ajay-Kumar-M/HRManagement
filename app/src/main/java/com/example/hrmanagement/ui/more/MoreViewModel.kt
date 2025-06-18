package com.example.hrmanagement.ui.more

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.R
import com.example.hrmanagement.Service.MyApplication.Companion.appUserEmailId
import com.example.hrmanagement.data.MoreItemData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MoreViewModel(
): ViewModel() {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0
//    var personEmailId: String = checkNotNull(savedStateHandle["personEmailId"])


    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

}