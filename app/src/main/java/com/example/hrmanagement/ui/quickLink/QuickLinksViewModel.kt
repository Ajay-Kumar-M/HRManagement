package com.example.hrmanagement.ui.quickLink

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.service.MyApplication.Companion.appDataManager
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuickLinksViewModel: ViewModel() {

    private var _quickLinksData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val quickLinksData = _quickLinksData.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFeatchProcess: Int = 0

    init {
        fetchQuickLinks()
    }

    fun fetchQuickLinks(){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.getAllQuickLinks(::updateQuickLinksData)
    }

    fun updateQuickLinksData(quickLinksData: QuerySnapshot?,response: String){
        if(response == "Success"){
            Log.d("MainScreenViewModel","updateQuickLinksData called $quickLinksData")
            quickLinksData?.count()?.let {
                if(it > 0) {
                    _quickLinksData.value = quickLinksData
                }
            }
        } else {
            //handle errors
            TODO()
        }
        numberOfFeatchProcess--
        if ((isViewLoading.value==true)&&(numberOfFeatchProcess==0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }
}