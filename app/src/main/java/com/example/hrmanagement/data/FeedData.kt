package com.example.hrmanagement.data

data class FeedData(
    val feedID: String = "",
    val username: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val timestamp: Long = 0L,
    val type: String = "",
    val message: String = "",
    val isLikeEnabled: Boolean = false,
    val isCommentEnabled: Boolean = false,
    var lastCommentId: Int = 0,
    var lastLikeId: Int = 0,
    var likesCount: Int = 0,
    var commentsCount: Int = 0,
    val likeUsers: MutableMap<String,LikeData> = mutableMapOf(),
    val comments: MutableMap<String,CommentsData> = mutableMapOf(),
    val otherData: Map<String,String> = mapOf()
)

data class FeedMetadata(
    val feedCount: Int = 0,
    val lastFeedId: Int = 0,
    val username: String = "",
    val email: String = "",
    val imageUrl: String = "",
)