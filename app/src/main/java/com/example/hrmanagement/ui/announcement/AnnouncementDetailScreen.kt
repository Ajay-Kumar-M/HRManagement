package com.example.hrmanagement.ui.announcement

import android.content.ClipData
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.ui.main.UserProfileImage
import com.example.hrmanagement.ui.main.formatTimestamp
import com.example.hrmanagement.ui.main.trimToLength
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AnnouncementDetailScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: AnnouncementDetailViewModel
) {
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val announcementData = viewModel.announcementData.collectAsStateWithLifecycle()
    val announcementComment = viewModel.announcementComment.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar =
            {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .background(Color.White)
                        .padding(10.dp,0.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Home Screen"
                        )
                    }
                    Box {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert, //ImageVector.vectorResource(R.drawable.),
                            contentDescription = "More",
                            modifier = Modifier.size(25.dp)
                                .clickable{
                                    expanded = true
                                }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Copy Post URL") },
                                onClick = {
                                    val job = scope.launch {
                                        val clipData = ClipData.newPlainText("announcementLink", AnnotatedString(announcementData.value.announcementLink))
                                        clipboardManager.setClipEntry(ClipEntry(clipData))
                                    }
                                    job.invokeOnCompletion {
                                        expanded = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
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
                announcementData.value.let{ announcement ->
                    Column(
                        modifier = Modifier
                            .padding(15.dp)
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize()
                            .weight(1f,true)
                    ) {
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            UserProfileImage(announcement.reporterProfileImageUrl)
                            Spacer(Modifier.width(10.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = announcement.title.trimToLength(25),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = formatTimestamp(announcement.date),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row {
                            Box(
                                modifier = Modifier
                                    .background(Color.Black, shape = RoundedCornerShape(16.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp
                                    )
                            ) {
                                Text(
                                    text = "Location",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp
                                    )
                            ) {
                                Text(
                                    text = announcement.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = announcement.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = announcement.category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = announcement.message,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            text = "Expiry : Nil",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red
                        )
                        Spacer(Modifier.height(20.dp))
                        if (!announcement.commentsEnabled) {
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
                                    text = "Comments for this post has been disabled",
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
                                contentDescription = "Home Screen",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = "${announcement.likesCount}",
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
                            val isAnnouncementLiked = announcement.likeUsers.values.any { it.emailId == viewModel.appUserData.email }
                            Row(
                                modifier = Modifier.clickable{
                                    viewModel.modifyLikeData(isAnnouncementLiked)
                                }
                            ) {
                                Icon(
                                    imageVector = if (isAnnouncementLiked) {
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
                        announcement.comments.forEach { comment ->
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
                                val isCommentLiked = comment.value.likeUsers.values.any{ it.emailId == viewModel.appUserData.email }
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
                Row (
                    modifier = Modifier.fillMaxWidth()
                        .height(50.dp)
                ) {
                    TextField(
                        value = announcementComment.value,
                        label = { Text("Comment") },
                        onValueChange = {
                            viewModel.onCommentChange(it)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if ((announcementComment.value.isNotBlank()) && (announcementData.value.commentsEnabled == true)) {
                                viewModel.addAnnouncementCommentTrigger()
                            } else {
                                Toast.makeText(context,"Either comments are disabled or comment is empty",Toast.LENGTH_LONG).show()
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