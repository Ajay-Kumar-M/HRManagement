package com.example.hrmanagement.ui.requests

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyRequestsViewModel: ViewModel() {

    private var _isPendingViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPendingViewLoading = _isPendingViewLoading.asStateFlow()
    var numberOfFeatchProcess: Int = 0

    init {

    }



    fun toggleIsPendingViewLoading(){
        _isPendingViewLoading.value = !_isPendingViewLoading.value
    }
}