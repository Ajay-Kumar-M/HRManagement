package com.example.hrmanagement.ui.more

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.component.completeFormatTimestamp
import com.example.hrmanagement.component.truncate
import com.example.hrmanagement.data.FeedData
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.data.LinkData
import com.example.hrmanagement.data.UserLoginData
import com.example.hrmanagement.ui.main.UserProfileImage
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun FeedsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: FeedsViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application))
) {

    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val viewRecords = viewModel.viewRecords.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                Text("Feeds",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("StatusScreen")
                }
            ){
                Icon(Icons.Filled.Add, contentDescription = "Add status")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isViewLoading.value){
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
                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                ) {
                    viewRecords.value.forEach { record ->
                        when(record.value){
                            is FeedData -> {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(5.dp)
                                            .background(Color.White,RoundedCornerShape(20.dp))
                                            .clickable{
                                                navController.navigate("FeedDetailScreen/${(record.value as FeedData).feedID}/Status")
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            UserProfileImage((record.value as FeedData).imageUrl)
                                            Column {
                                                Text(
                                                    text = "You have posted a message",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    modifier = Modifier.padding(20.dp,5.dp)
                                                )
                                                Text(
                                                    completeFormatTimestamp((record.value as FeedData).timestamp),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(20.dp,2.dp)
                                                )
                                                Text(
                                                    ((record.value as FeedData).message).truncate(20),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(20.dp,2.dp)
                                                )
                                            }
                                        }
                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(10.dp,0.dp)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(20.dp,0.dp)
                                                .fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ThumbUp,  //ImageVector.vectorResource(),
                                                contentDescription = "Like",
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                "Like",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(10.dp,5.dp)
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
                                                modifier = Modifier.padding(10.dp,5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            is LeaveData -> {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(5.dp)
                                            .background(Color.White,RoundedCornerShape(20.dp))
                                            .clickable{
                                                navController.navigate("FeedDetailScreen/${(record.value as LeaveData).leaveId}/LeaveRequest")
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth()
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
                                                                val userJson = Json.encodeToString(record.value as LeaveData)
                                                                val encodedUserJson =
                                                                    URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                                                                navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                                                            }
                                                        )
                                                    ) {
                                                        withStyle(style = SpanStyle(color = Color.Blue, fontSize = 15.sp)) {
                                                            append((record.value as LeaveData).leaveType)
                                                        }
                                                    }
                                                }
                                                Text(
                                                    text = annotatedString,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    modifier = Modifier.padding(20.dp,5.dp)
                                                )
                                                Text(
                                                    (record.value as LeaveData).dateOfRequestString,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(20.dp,2.dp)
                                                )
                                                Text(
                                                    "Date : ${(record.value as LeaveData).fromDateString} - ${(record.value as LeaveData).toDateString}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(20.dp,2.dp)
                                                )
                                                Text(
                                                    "Days : ${(record.value as LeaveData).numberOfDays}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(20.dp,2.dp)
                                                )
                                                Text(
                                                    "Reason : ${(record.value as LeaveData).reasonForLeave}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(20.dp,2.dp)
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(R.drawable.calendar_month),
                                                        contentDescription = "Calendar",
                                                        tint = Color.Unspecified,
                                                        modifier = Modifier.size(40.dp)
                                                            .padding(20.dp,5.dp,0.dp,5.dp)
                                                    )
                                                    Text(
                                                        "Leave Details",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.Blue,
                                                        modifier = Modifier.padding(5.dp,5.dp,0.dp,5.dp)
                                                            .clickable{
                                                                val userJson = Json.encodeToString(record.value as LeaveData)
                                                                val encodedUserJson =
                                                                    URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                                                                navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                        HorizontalDivider(
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(10.dp,0.dp)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(20.dp,0.dp)
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
                                                modifier = Modifier.padding(10.dp,5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}