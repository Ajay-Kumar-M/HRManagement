package com.example.hrmanagement.ui.holiday

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UpcomingHolidaysViewModel: ViewModel() {

    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFetchProcess: Int = 0
    private var _holidaysData: MutableStateFlow<QuerySnapshot?> = MutableStateFlow(null)
    val holidaysData = _holidaysData.asStateFlow()

    init {
        getHolidayDetails()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

    fun getHolidayDetails(){
        if (isViewLoading.value==false) toggleIsViewLoading()
        numberOfFetchProcess++
        appDataManager.getHolidays(::updateHolidayData)
    }

    fun updateHolidayData(holidays: QuerySnapshot?, response: String){
        Log.d("UpcomingHolidaysViewModel","updateHolidayData called $holidays")
        numberOfFetchProcess--
        if((response == "Success")&&(holidays!=null)){
            _holidaysData.value = holidays
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value==true)&&(numberOfFetchProcess==0))
            toggleIsViewLoading()
    }
}