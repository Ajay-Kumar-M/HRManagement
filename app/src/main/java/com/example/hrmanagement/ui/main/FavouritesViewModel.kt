package com.example.hrmanagement.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.FavoritePerson
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavouritesViewModel(application: Application): AndroidViewModel(application) {

    private var _favouritesData: MutableStateFlow<List<FavoritePerson>> = MutableStateFlow(listOf())
    val favouritesData = _favouritesData.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFetchProcess: Int = 0
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        fetchFavorites()
    }

    fun fetchFavorites() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getFirebaseUserFavorites(appUserData.email,::updateFavouritesData)

    }

    fun updateFavouritesData(favouritesData: QuerySnapshot?, response: String) {
        numberOfFetchProcess--
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateQuickLinksData called $favouritesData")
            favouritesData?.count()?.let {
                if (it > 0) {
                    _favouritesData.value = favouritesData.toObjects(FavoritePerson::class.java)
                } else {
                    _favouritesData.value = listOf()
                }
            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

}