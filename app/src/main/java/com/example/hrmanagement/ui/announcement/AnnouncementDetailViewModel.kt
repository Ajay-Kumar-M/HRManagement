package com.example.hrmanagement.ui.announcement

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.AnnouncementData
import com.example.hrmanagement.data.CommentsData
import com.example.hrmanagement.data.LikeData
import com.example.hrmanagement.data.UserLoginData
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class AnnouncementDetailViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private var _announcementComment: MutableStateFlow<String> = MutableStateFlow("")
    val announcementComment = _announcementComment.asStateFlow()
    private var _announcementData: MutableStateFlow<AnnouncementData> = MutableStateFlow(AnnouncementData())
    val announcementData = _announcementData.asStateFlow()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    val announcementId: Int = checkNotNull(savedStateHandle["announcementId"])
    private var numberOfFetchProcess: Int = 0
    var userEmailUiState: String? = ""
    var liveCommentID: Int = 0

    init {
        runBlocking {
            userEmailUiState = appPreferenceDataStore.emailFlow.firstOrNull()
        }
        fetchAnnouncementData(announcementId)
    }

    fun fetchAnnouncementData(announcementId: Int){
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getAnnouncementData(announcementId,::updateAnnouncementData)
    }

    fun updateAnnouncementData(announcementData: DocumentSnapshot?, response: String) {
        if (response == "Success") {
            Log.d("AnnouncementDetailViewModel", "updateAnnouncementsData called $announcementData")
            announcementData?.let { announcement ->
                _announcementData.value = announcement.toObject(AnnouncementData::class.java)!!
            }
        } else {
            //handle errors
            TODO()
        }
        numberOfFetchProcess--
        if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
            toggleIsViewLoading()
    }

    fun modifyLikeData(isAnnouncementLiked: Boolean){
        if ((userEmailUiState!=null)&&(userEmailUiState!!.isNotBlank())){
            if (!_isViewLoading.value) {
                toggleIsViewLoading()
            }
            numberOfFetchProcess++
            if (isAnnouncementLiked) {
                val currentData = _announcementData.value
                currentData.likeUsers.remove(currentData.likeUsers.filter { (_, value) -> value.emailId == userEmailUiState }.keys.first())
                currentData.likesCount = currentData.likesCount-1
                appDataManager.modifyAnnouncementData(currentData,::modifyLikeDataResponse)
            } else {
                appDataManager.getFirebaseUser(userEmailUiState!!,::addLikeData)
            }
        }
    }

    fun modifyLikeDataResponse(response: String){
        numberOfFetchProcess--
        if(response == "Success"){
            Log.d("AnnouncementDetailViewModel","modifyLikeDataResponse Like data added/removed")
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun addLikeData(userDetails: UserLoginData?, response: String){
        Log.d("AnnouncementDetailViewModel","updateUserDetails called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            val currentData = _announcementData.value
            currentData.likeUsers.put("${(currentData.lastLikeId)+1}", LikeData(
                userDetails.username,
                userEmailUiState!!,
                userDetails.imageUrl
                ))
            currentData.likesCount = currentData.likesCount+1
            currentData.lastLikeId = currentData.lastLikeId+1
            appDataManager.modifyAnnouncementData(currentData,::modifyLikeDataResponse)
        } else {
            //handle errors
            numberOfFetchProcess--
            if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
                toggleIsViewLoading()
            }
        }
    }

    fun modifyCommentLikeData(isCommentLiked: Boolean,commentID: Int){
        if ((userEmailUiState!=null)&&(userEmailUiState!!.isNotBlank())){
            if (!_isViewLoading.value) {
                toggleIsViewLoading()
            }
            numberOfFetchProcess++
            if (isCommentLiked) {
                var commentData = _announcementData.value.comments
//                currentData.likeUsers.remove(currentData.likeUsers.filter { (_, value) -> value.emailId == userEmailUiState }.keys.first())
                commentData.getValue(commentID.toString()).likeUsers.remove(commentData.getValue(commentID.toString()).likeUsers.filter { (_, value) -> value.emailId == userEmailUiState }.keys.first())
                commentData.getValue(commentID.toString()).likeCount = commentData.getValue(commentID.toString()).likeCount-1
                appDataManager.addAnnouncementCommentLikeData(commentData,_announcementData.value.announcementID,::modifyCommentLikeDataResponse)
            } else {
                liveCommentID = commentID
                appDataManager.getFirebaseUser(userEmailUiState!!,::addCommentLikeData)
            }
        }
    }

    fun modifyCommentLikeDataResponse(response: String){
        numberOfFetchProcess--
        if(response == "Success"){
            Log.d("AnnouncementDetailViewModel","modifyCommentLikeDataResponse Like data added/removed")
        } else {
            //handle errors
            TODO()
        }
        if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun addCommentLikeData(userDetails: UserLoginData?, response: String){
        Log.d("AnnouncementDetailViewModel","addCommentLikeData called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            var commentData = _announcementData.value.comments
            commentData[liveCommentID.toString()]?.let {
                it.likeUsers.put(
                    "${it.lastLikeId.plus(1)}",
                    LikeData(
                        userDetails.username,
                        userEmailUiState!!,
                        userDetails.imageUrl
                    )
                )
                it.lastLikeId = it.lastLikeId + 1
                it.likeCount = it.likeCount + 1
            }
            appDataManager.addAnnouncementCommentLikeData(commentData,_announcementData.value.announcementID,::modifyCommentLikeDataResponse)
        } else {
            //handle errors
            numberOfFetchProcess--
            if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
                toggleIsViewLoading()
            }
        }
        liveCommentID=0
    }

    fun addAnnouncementCommentTrigger(){
        if ((userEmailUiState!=null)&&(userEmailUiState!!.isNotBlank())) {
            if (!_isViewLoading.value) {
                toggleIsViewLoading()
            }
            numberOfFetchProcess++
            appDataManager.getFirebaseUser(userEmailUiState!!, ::addAnnouncementComment)
        }
    }

    fun addAnnouncementComment(userDetails: UserLoginData?, response: String){
        Log.d("AnnouncementDetailViewModel","addAnnouncementComment called $userDetails")
        if((response == "Success")&&(userDetails!=null)){
            var announcementData = _announcementData.value
            announcementData.commentsCount = announcementData.commentsCount + 1
            announcementData.lastCommentId = announcementData.lastCommentId+1
            announcementData.comments.put(
                "${announcementData.lastCommentId}",
                CommentsData(
                    announcementData.lastCommentId,
                    userDetails.username,
                    userDetails.email,
                    userDetails.imageUrl,
                    Calendar.getInstance().timeInMillis,
                    0,
                    mutableMapOf<String, LikeData>(),
                    announcementComment.value,
                    0
                )
            )
            appDataManager.modifyAnnouncementData(announcementData,::addCommentDataResponse)
        } else {
            //handle errors
            numberOfFetchProcess--
            if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
                toggleIsViewLoading()
            }
        }
    }

    fun addCommentDataResponse(response: String){
        numberOfFetchProcess--
        if(response == "Success"){
            Log.d("AnnouncementDetailViewModel","addCommentDataResponse comment data added")
        } else {
            //handle errors
            TODO()
        }
        _announcementComment.value = ""
        if((isViewLoading.value)&&(numberOfFetchProcess==0)) {
            toggleIsViewLoading()
        }
    }

    fun toggleIsViewLoading() {
        _isViewLoading.value = !_isViewLoading.value
    }

    fun onCommentChange(newText: String) {
        _announcementComment.value = newText
    }

}