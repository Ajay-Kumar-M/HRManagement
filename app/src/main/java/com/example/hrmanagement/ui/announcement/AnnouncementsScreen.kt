package com.example.hrmanagement.ui.announcement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.AnnouncementList
import com.example.hrmanagement.ui.main.formatTimestamp
import com.example.hrmanagement.ui.main.trimToLength
import androidx.compose.runtime.livedata.observeAsState
import com.example.hrmanagement.ui.main.UserProfileImage
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun AnnouncementsScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: AnnouncementsViewModel = viewModel()
) {
    val filteredAnnouncementsData = viewModel.filteredAnnouncementsData.collectAsStateWithLifecycle()
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val filterResult = navController.currentBackStackEntry
        ?.savedStateHandle?.getLiveData<List<Map<String, Boolean>>>("filters")?.observeAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    filterResult?.value?.let { value ->
                        viewModel.filterAnnouncementData(value)
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
    modifier = Modifier.padding(5.dp),
    contentWindowInsets = WindowInsets.safeDrawing,
        topBar =
        {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .background(Color.White)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                Text(
                    "Announcements",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 5.dp)
                        .weight(1f,true)
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.filter),
                    contentDescription = "Filter",
                    modifier = Modifier.size(20.dp)
                        .clickable{
                            if (filterResult?.value==null){
                                navController.navigate("AnnouncementsFilterScreen")
                            } else {
                                val filterList = Json.encodeToString(filterResult.value)
                                val encodedLeaveJson =
                                    URLEncoder.encode(filterList, StandardCharsets.UTF_8.toString())
                                navController.navigate("AnnouncementsFilterScreen/${encodedLeaveJson}")
                            }
                        }
                )
                Spacer(Modifier.width(10.dp))
            }
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(15.dp))
                .fillMaxSize()
                .background(Color.White)
//                                .verticalScroll(rememberScrollState())
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
                filteredAnnouncementsData.value?.let { it ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        items(it) { announcement ->
                                Row (
                                    modifier = Modifier.padding(10.dp)
                                        .fillMaxWidth()
                                        .clickable{
                                            navController.navigate("AnnouncementDetailScreen/${announcement.announcementID}")
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ){
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
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = announcement.category,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Spacer(Modifier.width(10.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.ThumbUp, //ImageVector.vectorResource(R.drawable.mic),
                                        contentDescription = "Likes",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "${announcement.likesCount}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.comment_24dp),
                                        contentDescription = "Comments",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        text = "${announcement.commentsCount}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                            Spacer(Modifier.width(10.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
