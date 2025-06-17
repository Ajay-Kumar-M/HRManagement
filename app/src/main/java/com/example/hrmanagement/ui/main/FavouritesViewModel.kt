package com.example.hrmanagement.ui.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.FavoritePerson
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavouritesViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private var _favouritesData: MutableStateFlow<List<FavoritePerson>> = MutableStateFlow(listOf())
    val favouritesData = _favouritesData.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFeatchProcess: Int = 0
    var userEmailId: String = checkNotNull(savedStateHandle["userEmailId"])

    init {
        fetchFavorites()
    }

    fun fetchFavorites() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFeatchProcess++
        appDataManager.getFirebaseUserFavorites(userEmailId,::updateFavouritesData)

    }

    fun updateFavouritesData(favouritesData: QuerySnapshot?, response: String) {
        numberOfFeatchProcess--
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateQuickLinksData called $favouritesData")
            favouritesData?.count()?.let {
                if (it > 0) {
                    _favouritesData.value = favouritesData.toObjects(FavoritePerson::class.java)
                }
            }
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value == true) && (numberOfFeatchProcess == 0))
            toggleIsViewLoading()
    }

    fun toggleIsViewLoading(){
        _isViewLoading.value = !_isViewLoading.value
    }

}