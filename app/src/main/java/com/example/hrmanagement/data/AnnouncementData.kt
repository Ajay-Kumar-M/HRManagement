package com.example.hrmanagement.data

import kotlinx.serialization.Serializable
import java.util.Calendar

data class AnnouncementList(
    val announcementID: Int = 0,
    val title: String = "",
    val date: Long = 0L,
    val category: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val reporterEmailId: String = "",
    val reporterName: String = "",
    val reporterProfileImageUrl: String = "",
    var announcementPinned: Boolean = false,
    val location: String = ""
)


data class AnnouncementData(
    val announcementID: Int = 0,
    val title: String = "",
    val date: Long = 0L,
    val category: String = "",
    val location: String = "",
    val message: String = "",
    val announcementLink: String = "",
    val expiry: Long = 0L,
    val status: String = "",
    var announcementPinned: Boolean = false,
    val commentsEnabled: Boolean = true,
    var lastCommentId: Int = 0,
    var lastLikeId: Int = 0,
    var likesCount: Int = 0,
    var commentsCount: Int = 0,
    val likeUsers: MutableMap<String,LikeData> = mutableMapOf(),
    val comments: MutableMap<String,CommentsData> = mutableMapOf(),
    val reporterEmailId: String = "",
    val reporterName: String = "",
    val reporterProfileImageUrl: String = "",
)

@Serializable
data class CommentsData(
    val commentId: Int = 0,
    val username: String = "",
    val emailId: String = "",
    val userProfileImageUrl: String = "",
    val date: Long = 0L,
    var likeCount: Int = 0,
    val likeUsers: MutableMap<String,LikeData> = mutableMapOf(),
    val comment: String = "",
    var lastLikeId: Int = 0
)

@Serializable
data class LikeData(
    val username: String = "",
    val emailId: String = "",
    val userProfileImageUrl: String = "",
)