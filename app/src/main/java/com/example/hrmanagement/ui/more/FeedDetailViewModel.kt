package com.example.hrmanagement.ui.more

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.Service.MyApplication.Companion.appPreferenceDataStore
import com.example.hrmanagement.data.CommentsData
import com.example.hrmanagement.data.FeedData
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.LikeData
import com.example.hrmanagement.data.UserLoginData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class FeedDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private var _feedComment: MutableStateFlow<String> = MutableStateFlow("")
    val feedComment = _feedComment.asStateFlow()
    private var _leaveRecord: MutableStateFlow<LeaveData> = MutableStateFlow(LeaveData())
    val leaveRecord = _leaveRecord.asStateFlow()
    private var _feedRecord: MutableStateFlow<FeedData> = MutableStateFlow(FeedData())
    val feedRecord = _feedRecord.asStateFlow()
    var userLoginData = UserLoginData()
    private var _isViewLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isViewLoading = _isViewLoading.asStateFlow()
    var numberOfFetchProcess: Int = 0
    var feedType: String = ""
    var leaveId: Int = 0
    var feedId: Int = 0
    private val myApplication = application as MyApplication
    val appUserData = myApplication.appUserDetails

    init {
        feedType = checkNotNull(savedStateHandle["feedType"])
        fetchUserDetails()
        if (feedType == "LeaveRequest") {
            leaveId = checkNotNull(savedStateHandle["feedId"])
            fetchLeaveRequestData()
        }
        if (feedType == "Status") {
            feedId = checkNotNull(savedStateHandle["feedId"])
            fetchFeedRequestData()
        }
    }

    fun fetchUserDetails() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getFirebaseUser(appUserData.email, ::updateUserDetails)
    }

    fun updateUserDetails(userDetails: UserLoginData?, response: String) {
        numberOfFetchProcess--
        Log.d("FeedsViewModel", "updateUserDetails called $userDetails")
        if ((response == "Success") && (userDetails != null)) {
            userLoginData = userDetails
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value) && (numberOfFetchProcess == 0)) {
            toggleIsViewLoading()
        }
    }

    fun fetchLeaveRequestData() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.fetchLeaveLogs(
            2025,
            appUserData.email,
            leaveId
        ) { querySnapshot, response, documentSnapshot ->
            Log.d("FeedsViewModel", "fetchLeaveRequestData response called $response")
            if ((response == "Success") && (documentSnapshot != null)) {
                _leaveRecord.value = documentSnapshot.toObject(LeaveData::class.java) ?: LeaveData()
                Log.d("FeedsViewModel", "fetchLeaveRequestData response called ${_leaveRecord.value}")

            } else {
            }
            numberOfFetchProcess--
            if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
                toggleIsViewLoading()
        }
    }

    fun fetchFeedRequestData() {
        if (!_isViewLoading.value) {
            toggleIsViewLoading()
        }
        numberOfFetchProcess++
        appDataManager.getUserFeedID(appUserData.email, feedId) { documentSnapshot, response ->
            Log.d("FeedsViewModel", "fetchLeaveRequestsData response called $response")
            if ((response == "Success") && (documentSnapshot != null)) {
                _feedRecord.value = documentSnapshot.toObject(FeedData::class.java) ?: FeedData()
                Log.d("FeedsViewModel", "fetchLeaveRequestsData response called ${_feedRecord.value}")
            } else {
            }
            numberOfFetchProcess--
            if ((isViewLoading.value == true) && (numberOfFetchProcess == 0))
                toggleIsViewLoading()
        }
    }

    fun addFeedComment() {
        Log.d("FeedDetailViewModel", "addFeedCommentTrigger called")
        when (feedType) {
            "LeaveRequest" -> {
                var leaveData = _leaveRecord.value
                leaveData.commentsCount = leaveData.commentsCount + 1
                leaveData.lastCommentId = leaveData.lastCommentId + 1
                leaveData.comments.put(
                    "${leaveData.lastCommentId}",
                    CommentsData(
                        leaveData.lastCommentId,
                        userLoginData.username,
                        userLoginData.email,
                        userLoginData.imageUrl,
                        Calendar.getInstance().timeInMillis,
                        0,
                        mutableMapOf<String, LikeData>(),
                        feedComment.value,
                        0
                    )
                )
                Log.d("FeedsViewModel", "addFeedComment called ${leaveData}")
                appDataManager.addLeaveData(leaveData, ::addFeedDataResponse)
            }

            "Status" -> {
                var feedData = _feedRecord.value
                feedData.commentsCount = feedData.commentsCount + 1
                feedData.lastCommentId = feedData.lastCommentId + 1
                feedData.comments.put(
                    "${feedData.lastCommentId}",
                    CommentsData(
                        feedData.lastCommentId,
                        userLoginData.username,
                        userLoginData.email,
                        userLoginData.imageUrl,
                        Calendar.getInstance().timeInMillis,
                        0,
                        mutableMapOf<String, LikeData>(),
                        feedComment.value,
                        0
                    )
                )
                appDataManager.setUserFeed(feedData, ::addFeedDataResponse)
            }
        }
    }

    fun addFeedDataResponse(response: String) {
        numberOfFetchProcess--
        if (response == "Success") {
            Log.d("FeedDetailViewModel", "addFeedDataResponse comment or like data added")
        } else {
            //handle errors
            TODO()
        }
        _feedComment.value = ""
        if ((isViewLoading.value) && (numberOfFetchProcess == 0)) {
            toggleIsViewLoading()
        }
    }

    fun modifyCommentLikeData(isCommentLiked: Boolean, commentID: Int) {
        if (appUserData.email.isNotBlank()) {
            if (!_isViewLoading.value) {
                toggleIsViewLoading()
            }
            numberOfFetchProcess++
            if (isCommentLiked) {
                when (feedType) {
                    "LeaveRequest" -> {
                        var commentData = _leaveRecord.value.comments
//                currentData.likeUsers.remove(currentData.likeUsers.filter { (_, value) -> value.emailId == userEmailUiState }.keys.first())
                        commentData.getValue(commentID.toString()).likeUsers.remove(
                            commentData.getValue(
                                commentID.toString()
                            ).likeUsers.filter { (_, value) -> value.emailId == appUserData.email }.keys.first()
                        )
                        commentData.getValue(commentID.toString()).likeCount =
                            commentData.getValue(commentID.toString()).likeCount - 1
                        appDataManager.addLeaveDataCommentLikeData(
                            commentData,
                            _leaveRecord.value.leaveId,
                            appUserData.email,
                            ::modifyCommentLikeDataResponse
                        )
                    }

                    "Status" -> {
                        var commentData = _feedRecord.value.comments
//                currentData.likeUsers.remove(currentData.likeUsers.filter { (_, value) -> value.emailId == userEmailUiState }.keys.first())
                        commentData.getValue(commentID.toString()).likeUsers.remove(
                            commentData.getValue(
                                commentID.toString()
                            ).likeUsers.filter { (_, value) -> value.emailId == appUserData.email }.keys.first()
                        )
                        commentData.getValue(commentID.toString()).likeCount =
                            commentData.getValue(commentID.toString()).likeCount - 1
                        appDataManager.addFeedDataCommentLikeData(
                            commentData,
                            _feedRecord.value.feedID.toInt(),
                            appUserData.email,
                            ::modifyCommentLikeDataResponse
                        )
                    }
                }
            } else {
                when (feedType) {
                    "LeaveRequest" -> {
                        var commentData = _leaveRecord.value.comments
                        commentData[commentID.toString()]?.let {
                            it.likeUsers.put(
                                "${it.lastLikeId.plus(1)}",
                                LikeData(
                                    userLoginData.username,
                                    userLoginData.email,
                                    userLoginData.imageUrl
                                )
                            )
                            it.lastLikeId = it.lastLikeId + 1
                            it.likeCount = it.likeCount + 1
                        }
                        appDataManager.addLeaveDataCommentLikeData(
                            commentData,
                            _leaveRecord.value.leaveId,
                            appUserData.email,
                            ::modifyCommentLikeDataResponse
                        )
                    }

                    "Status" -> {
                        var commentData = _feedRecord.value.comments
                        commentData[commentID.toString()]?.let {
                            it.likeUsers.put(
                                "${it.lastLikeId.plus(1)}",
                                LikeData(
                                    userLoginData.username,
                                    userLoginData.email,
                                    userLoginData.imageUrl
                                )
                            )
                            it.lastLikeId = it.lastLikeId + 1
                            it.likeCount = it.likeCount + 1
                        }
                        appDataManager.addLeaveDataCommentLikeData(
                            commentData,
                            _feedRecord.value.feedID.toInt(),
                            appUserData.email,
                            ::modifyCommentLikeDataResponse
                        )
                    }
                }
            }
        }
    }

    fun modifyCommentLikeDataResponse(response: String) {
        numberOfFetchProcess--
        if (response == "Success") {
            Log.d("FeedDetailViewModel", "modifyCommentLikeDataResponse Like data added/removed")
        } else {
            //handle errors
            TODO()
        }
        if ((isViewLoading.value) && (numberOfFetchProcess == 0)) {
            toggleIsViewLoading()
        }
    }


    fun modifyLikeData(isFeedLiked: Boolean) {
        if (appUserData.email.isNotBlank()) {
            if (!_isViewLoading.value) {
                toggleIsViewLoading()
            }
            numberOfFetchProcess++
            if (isFeedLiked) {
                val currentData = _feedRecord.value
                currentData.likeUsers.remove(currentData.likeUsers.filter { (_, value) -> value.emailId == appUserData.email }.keys.first())
                currentData.likesCount = currentData.likesCount - 1
                appDataManager.setUserFeed(currentData, ::addFeedDataResponse)
            } else {
                val currentData = _feedRecord.value
                currentData.likeUsers.put("${(currentData.lastLikeId)+1}", LikeData(
                    userLoginData.username,
                    userLoginData.email,
                    userLoginData.imageUrl
                ))
                currentData.likesCount = currentData.likesCount+1
                currentData.lastLikeId = currentData.lastLikeId+1
                appDataManager.setUserFeed(currentData, ::addFeedDataResponse)
            }
        }
    }

    fun onCommentChange(newComment: String) {
        _feedComment.value = newComment
    }

    fun toggleIsViewLoading() {
        _isViewLoading.value = !_isViewLoading.value
    }
}