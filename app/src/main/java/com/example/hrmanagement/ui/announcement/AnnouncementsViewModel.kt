package com.example.hrmanagement.ui.announcement

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.data.AnnouncementList
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnnouncementsViewModel(): ViewModel() {

    var unfilteredAnnouncementListData = mutableListOf<AnnouncementList>()
    private var _filteredAnnouncementsData: MutableStateFlow<List<AnnouncementList>?> = MutableStateFlow(null)
    val filteredAnnouncementsData = _filteredAnnouncementsData.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    private var numberOfFetchProcess: Int = 0

    init {
        fetchAnnouncements()
    }

    fun fetchAnnouncements() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getAnnouncements(::updateAnnouncementsData)
    }

    fun updateAnnouncementsData(announcementData: QuerySnapshot?, response: String) {
        if (response == "Success") {
            Log.d("MainScreenViewModel", "updateAnnouncementsData called $announcementData")
            unfilteredAnnouncementListData.clear()
            announcementData?.forEach { announcement ->
                unfilteredAnnouncementListData.add(announcement.toObject(AnnouncementList::class.java))
            }
            _filteredAnnouncementsData.value = unfilteredAnnouncementListData
        } else {
            //handle errors
            TODO()
        }
        numberOfFetchProcess--
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }

    fun filterAnnouncementData(selectedFilters: List<Map<String, Boolean>>){
        val categoryFilter = selectedFilters.get(0).filter {
            it.value == true
        }
        val locationFilter = selectedFilters.get(1).filter {
            it.value == true
        }
//        var temp = mutableListOf<AnnouncementList>()
//        if (categoryFilter.isNotEmpty()) {
//            temp.addAll(unfilteredAnnouncementListData.filter {
//                it.category in categoryFilter.keys
//            })
//        }
//        if (locationFilter.isNotEmpty()) {
//            temp.addAll(unfilteredAnnouncementListData.filter {
//                it.location in locationFilter.keys
//            })
//        }
        val temp = unfilteredAnnouncementListData.filter { announcement ->
            (categoryFilter.isEmpty() || announcement.category in categoryFilter.keys) &&
                    (locationFilter.isEmpty() || announcement.location in locationFilter.keys)
        }.toMutableList()
        _filteredAnnouncementsData.value = temp
    }

    fun toggleIsViewLoading() {
        _isViewLoading.value = !_isViewLoading.value
    }

}

/*

fun search() {
        toggleCircularProgressIndicator()
        if(contactSearchString.text.isNotEmpty())
        {
            _filterContact.clear()
            _filterContact.addAll(
                deviceContactsList.filter {
                    it.displayName.contains(contactSearchString.text, true) or it.phoneNumber.contains(contactSearchString.text, true)
                }
            )

        }
        toggleCircularProgressIndicator()
    }

    private var _filterContact = mutableListOf<Contact>().toMutableStateList()
    val filterContact: List<Contact>
        get() = _filterContact.toList()

    private var deviceContactsList: MutableList<Contact> = mutableListOf()

*/