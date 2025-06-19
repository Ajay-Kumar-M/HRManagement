package com.example.hrmanagement.ui.more

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.component.completeFormatTimestamp
import com.example.hrmanagement.component.truncate
import com.example.hrmanagement.data.FeedData
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.ui.main.UserProfileImage
import com.example.hrmanagement.ui.main.formatTimestamp
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun FeedDetailScreen(
    modifier: Modifier,
    navController: NavController,
    userEmailId: String,
    feedId: Int,
    feedType: String,
    viewModel: FeedDetailViewModel = viewModel()
) {

    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val leaveRecord = viewModel.leaveRecord.collectAsStateWithLifecycle()
    val feedRecord = viewModel.feedRecord.collectAsStateWithLifecycle()
    val feedComment = viewModel.feedComment.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth(),
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous screen"
                    )
                }
                Text(
                    "Feeds",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 5.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isViewLoading.value) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicatorComposable()
                }
            } else {
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (feedType) {
                        "LeaveRequest" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                UserProfileImage(viewModel.userLoginData.imageUrl)
                                Column {
                                    val annotatedString = buildAnnotatedString {
                                        append("You have raised an request for ")
                                        withLink(
                                            LinkAnnotation.Clickable(
                                                tag = "Leave_Data_CLICK",
                                                linkInteractionListener = {
                                                    val userJson =
                                                        Json.encodeToString(leaveRecord.value)
                                                    val encodedUserJson =
                                                        URLEncoder.encode(
                                                            userJson,
                                                            StandardCharsets.UTF_8.toString()
                                                        )
                                                    navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                                                }
                                            )
                                        ) {
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color.Blue,
                                                    fontSize = 15.sp
                                                )
                                            ) {
                                                append(leaveRecord.value.leaveType)
                                            }
                                        }
                                    }
                                    Text(
                                        text = annotatedString,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(20.dp, 5.dp)
                                    )
                                    Text(
                                        leaveRecord.value.dateOfRequestString,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(20.dp, 2.dp)
                                    )
                                    Text(
                                        "Date : ${leaveRecord.value.fromDateString} - ${leaveRecord.value.toDateString}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(20.dp, 2.dp)
                                    )
                                    Text(
                                        "Days : ${leaveRecord.value.numberOfDays}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(20.dp, 2.dp)
                                    )
                                    Text(
                                        "Reason : ${leaveRecord.value.reasonForLeave}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(20.dp, 2.dp)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(R.drawable.calendar_month),
                                            contentDescription = "Calendar",
                                            tint = Color.Unspecified,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .padding(20.dp, 5.dp, 0.dp, 5.dp)
                                        )
                                        Text(
                                            "Leave Details",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Blue,
                                            modifier = Modifier
                                                .padding(5.dp, 5.dp, 0.dp, 5.dp)
                                                .clickable {
                                                    val userJson =
                                                        Json.encodeToString(leaveRecord.value)
                                                    val encodedUserJson =
                                                        URLEncoder.encode(
                                                            userJson,
                                                            StandardCharsets.UTF_8.toString()
                                                        )
                                                    navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                                                }
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(20.dp, 0.dp)
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.comment_24dp),
                                    contentDescription = "Comment",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Comment",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(10.dp, 5.dp)
                                )
                            }
                            Spacer(Modifier.height(20.dp))
                            if (!leaveRecord.value.isCommentsEnabled) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color(0xFFFFD6D7)),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lock, //ImageVector.vectorResource(R.drawable.like_facebook),
                                        contentDescription = "Lock",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "Comments for this feed has been disabled",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                            HorizontalDivider(thickness = 2.dp)

                            Spacer(Modifier.height(10.dp))
                            leaveRecord.value.comments.forEach { comment ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                                        .padding(10.dp)
                                ){
                                    Row (
                                        modifier = Modifier.fillMaxWidth()
                                    ){
                                        UserProfileImage(comment.value.userProfileImageUrl)
                                        Spacer(Modifier.width(10.dp))
                                        Column(
                                        ) {
                                            Text(
                                                text = comment.value.username,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = comment.value.comment,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(5.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(15.dp,0.dp)
                                ) {
                                    val isCommentLiked = comment.value.likeUsers.values.any{ it.emailId == viewModel.userEmail }
                                    Text(
                                        text = if(isCommentLiked) {
                                            "Liked"
                                        } else {
                                            "Like"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.clickable{
                                            viewModel.modifyCommentLikeData(isCommentLiked,comment.value.commentId)
                                        }
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = " - ${comment.value.likeCount}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.width(30.dp))
                                    Text(
                                        text = formatTimestamp(comment.value.date),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(Modifier.height(15.dp))
                            }
                        }

                        "Status" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            ) {
                                UserProfileImage(feedRecord.value.imageUrl)
                                Column {
                                    Text(
                                        text = "You have posted a message",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(20.dp, 5.dp)
                                    )
                                    Text(
                                        completeFormatTimestamp(feedRecord.value.timestamp),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(20.dp, 2.dp)
                                    )
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(4.dp, 0.dp)
                                    )
                                    Text(
                                        feedRecord.value.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(20.dp, 2.dp)
                                    )
                                }
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                modifier = Modifier.padding(10.dp, 0.dp)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(20.dp, 0.dp)
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ThumbUp,
                                    contentDescription = "Like",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Like",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(10.dp, 5.dp)
                                )
                                Spacer(modifier = Modifier.width(35.dp))
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.comment_24dp),
                                    contentDescription = "Comment",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Comment",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(10.dp, 5.dp)
                                )
                            }

                            Spacer(Modifier.height(20.dp))
                            if (!feedRecord.value.isCommentEnabled) {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color(0xFFFFD6D7)),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lock, //ImageVector.vectorResource(R.drawable.like_facebook),
                                        contentDescription = "Lock",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "Comments for this feed has been disabled",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                            }
                            HorizontalDivider(thickness = 2.dp)
                            Spacer(Modifier.height(5.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.like_facebook),
                                    contentDescription = "Like",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "${feedRecord.value.likesCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Red
                                )
                            }
                            Spacer(Modifier.height(5.dp))
                            HorizontalDivider(thickness = 2.dp)
                            Spacer(Modifier.height(5.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isFeedLiked = feedRecord.value.likeUsers.values.any { it.emailId == viewModel.userEmail }
                                Row(
                                    modifier = Modifier.clickable{
                                        viewModel.modifyLikeData(isFeedLiked)
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isFeedLiked) {
                                            Icons.Filled.ThumbUp
                                        } else {
                                            Icons.Outlined.ThumbUp
                                        },
                                        contentDescription = "Like"
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "Like",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(Modifier.width(50.dp))
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.comment_24dp),
                                    contentDescription = "Comment"
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "Comment",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(Modifier.height(5.dp))
                            HorizontalDivider(thickness = 2.dp)
                            Spacer(Modifier.height(10.dp))
                            feedRecord.value.comments.forEach { comment ->
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                                        .padding(10.dp)
                                ){
                                    Row (
                                        modifier = Modifier.fillMaxWidth()
                                    ){
                                        UserProfileImage(comment.value.userProfileImageUrl)
                                        Spacer(Modifier.width(10.dp))
                                        Column(
                                        ) {
                                            Text(
                                                text = comment.value.username,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = comment.value.comment,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(5.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(15.dp,0.dp)
                                ) {
                                    val isCommentLiked = comment.value.likeUsers.values.any{ it.emailId == viewModel.userEmail }
                                    Text(
                                        text = if(isCommentLiked) {
                                            "Liked"
                                        } else {
                                            "Like"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.clickable{
                                            viewModel.modifyCommentLikeData(isCommentLiked,comment.value.commentId)
                                        }
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Text(
                                        text = " - ${comment.value.likeCount}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.width(30.dp))
                                    Text(
                                        text = formatTimestamp(comment.value.date),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(Modifier.height(15.dp))
                            }
                        }
                    }
                }
                Row (
                    modifier = Modifier.fillMaxWidth()
                        .height(50.dp)
                ) {
                    TextField(
                        value = feedComment.value,
                        label = { Text("Comment") },
                        onValueChange = {
                            viewModel.onCommentChange(it)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (feedComment.value.isNotBlank()){
                                if ((feedType == "LeaveRequest")&&(leaveRecord.value.isCommentsEnabled == true)) {
                                    viewModel.addFeedComment()
                                } else if ((feedType == "Status")&&(feedRecord.value.isCommentEnabled == true)) {
                                    viewModel.addFeedComment()
                                } else {
                                    Toast.makeText(context,"Comments are disabled for this feed.",Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context,"Please enter comment!",Toast.LENGTH_LONG).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.send_alt),
                            contentDescription = "Add comment",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}