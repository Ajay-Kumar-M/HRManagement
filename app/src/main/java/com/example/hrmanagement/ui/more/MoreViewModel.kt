package com.example.hrmanagement.ui.more

import androidx.lifecycle.ViewModel
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