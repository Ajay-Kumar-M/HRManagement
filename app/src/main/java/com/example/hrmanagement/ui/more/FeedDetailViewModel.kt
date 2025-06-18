package com.example.hrmanagement.ui.more

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedDetailViewModel: ViewModel() {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0


    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}