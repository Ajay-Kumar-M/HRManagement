package com.example.hrmanagement.ui.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.hrmanagement.R
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.AnnouncementList
import com.example.hrmanagement.data.HolidayData
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.data.LinkData
import com.example.hrmanagement.data.UserLoginData
import com.example.hrmanagement.ui.userinfo.AttendanceFilterShowModalSheet
import com.example.hrmanagement.ui.userinfo.LeaveTrackerShowModalSheet
import com.example.hrmanagement.ui.userinfo.UserInfoScreenViewModel
import com.example.hrmanagement.ui.userinfo.getPropertyValue
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: MainScreenViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var fabClicks by remember { mutableIntStateOf(0) }

    val userImageUri = viewModel.userImageUriUiState.collectAsStateWithLifecycle()
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val liveUserDetails: State<UserLoginData> = viewModel.liveUserDetails.collectAsStateWithLifecycle()
    var selectedItem by remember { mutableIntStateOf(1) }
    val items = listOf("Services", "Home", "", "Approvals", "More")
    val selectedIcons = listOf(ImageVector.vectorResource(id = R.drawable.apps),Icons.Filled.Home,Icons.Filled.AddCircle, Icons.Rounded.CheckCircle, Icons.Rounded.MoreVert)
    val unselectedIcons = listOf(ImageVector.vectorResource(id = R.drawable.drag_indicator),Icons.Outlined.Home,Icons.Outlined.AddCircle, Icons.Outlined.CheckCircle, Icons.Outlined.MoreVert)
    val addTaskShowBottomSheet = viewModel.addTaskShowBottomSheet.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp, 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Row {
                            AsyncImage(
                                model = if(userImageUri.value.isNullOrBlank()) {
                                    Log.d("MainScreen","if block ${userImageUri.value}")
                                    R.drawable.account_circle_24
                                } else {
                                    Log.d("MainScreen","else block ${userImageUri.value}")
                                    userImageUri.value
                                },
                                contentDescription = "Profile Icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(16.dp)) // Rounded corners
                                    .clickable(onClick = {
                                        val userJson =
                                            Json.encodeToString(liveUserDetails.value)
                                        val encodedUserJson = URLEncoder.encode(
                                            userJson,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("UserInfoScreen/$encodedUserJson")
                                    })
                            )

                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Home",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(10.dp,5.dp,0.dp,0.dp)
                            )
                        }
                        Row {
                            IconButton(
                                onClick = {  },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFFF2F2F2),
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Favorite"
                                )
                            }
                            IconButton(
                                onClick = {  },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFFF2F2F2),
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Favorite"
                                )
                            }
                        }
                    }
                        },
//                    navigationIcon = {
//                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
//                            Icon(Icons.Default.Menu, contentDescription = "Menu")
//                        }
//                    },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF2F2F2),
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFFF2F2F2),
                contentColor = Color.White
            ) {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                    contentDescription = item,
                                    modifier = if (index == 2) Modifier.size(35.dp) else Modifier.size(25.dp)
                                )
                            },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = {
                                if (index==2){
                                    viewModel.toggleAddTaskShowBottomSheet()
                                } else {
                                    selectedItem = index
                                }
                            }
                        )
                    }
                }
                if (addTaskShowBottomSheet.value) AddTaskShowModalSheet(navController,viewModel)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { outerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isViewLoading.value){
                Column(
                    modifier = Modifier
                        .padding(outerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicatorComposable()
                }
            } else {
                when(selectedItem){
                    0 -> {

                    }
                    1 -> {
                        HomeScreen(outerPadding,navController,viewModel)
                    }
                    3 -> {

                    }
                    4 -> {

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    outerPadding: PaddingValues,
    navController: NavController,
    viewModel: MainScreenViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isPullDownRefreshing = remember { mutableStateOf(false) }
    val quickLinksData = viewModel.quickLinksLimitedData.collectAsStateWithLifecycle()
    val leaveTrackerDetails = viewModel.liveLeaveTrackerDetails.collectAsStateWithLifecycle()
    val announcementsLimitedData = viewModel.announcementsLimitedData.collectAsStateWithLifecycle()
    val holidaysData = viewModel.holidaysData.collectAsStateWithLifecycle()
    val liveUserDetails: State<UserLoginData> = viewModel.liveUserDetails.collectAsStateWithLifecycle()
    val leaveTypes = listOf("Casual Leave", "Sick Leave", "On Duty")
    val leaveTypeIcons: Map<String, ImageVector> = mapOf(
        Pair("Casual Leave",ImageVector.vectorResource(id = R.drawable.leave_casual)),
        Pair("Sick Leave",ImageVector.vectorResource(id = R.drawable.plus_green)),
        Pair("On Duty",ImageVector.vectorResource(id = R.drawable.icons8_person)))
    val leaveTypeDataClassMap: Map<String, String> = mapOf(
        Pair("Casual LeaveBooked","casualLeaveBooked"),
        Pair("Casual LeaveBalance","casualLeaveBalance"),
        Pair("Sick LeaveBooked","sickLeaveBooked"),
        Pair("Sick LeaveBalance","sickLeaveBalance"),
        Pair("On DutyBooked","onDutyLeaveBooked"),
        Pair("On DutyBalance","onDutyLeaveBalance"))

    PullToRefreshBox(
        isRefreshing = isPullDownRefreshing.value,
        onRefresh = {
            viewModel.fetchUserSignInStatus()
            viewModel.fetchLimitedQuickLinks()
            viewModel.fetchLimitedAnnouncements()
            viewModel.getLeaveTrackerDetails()
            viewModel.getHolidayDetails()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(outerPadding),
//            .weight(weight = 1f, fill = false),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            SignInStatus(viewModel)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .fillMaxWidth(0.8f)
                    .height(200.dp)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder, //ImageVector.vectorResource(R.drawable.clock_24dp),
                        contentDescription = "Favorites",
                        tint = Color(0xFFADD8E6)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Favorites",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.height(10.dp))
                if (false) {

                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "No Data Found",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .fillMaxWidth(0.8f)
                    .height(250.dp)
                    .background(Color.White)
//                                .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.link_header_icon),
                        contentDescription = "Quick Links",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Quick Links",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.height(5.dp))
                quickLinksData.value?.let {
                    if (it.size() > 0) {
                        it.forEach { quickLink ->
                            val linkData = quickLink.toObject(LinkData::class.java)
                            Row (
                                modifier = Modifier.padding(10.dp)
                            ){
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.link),
                                    contentDescription = "Links",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    buildAnnotatedString {
                                        withLink(
                                            LinkAnnotation.Url(
                                                url = linkData.linkurl,
                                                styles = TextLinkStyles(style = SpanStyle(color = Color.Blue))
                                            )
                                        ) {
                                            append(linkData.linkname.trimToLength(20))
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.width(20.dp))
                        Button(
                            onClick = {
                                quickLinksData.value?.let {
                                    navController.navigate("QuickLinksScreen")
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFADD8E6)
                            ),
                        ) {
                            Text(
                                text = "View More",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "No Data Found",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .fillMaxWidth(0.8f)
                    .height(370.dp)
                    .background(Color.White)
//                                .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.mic),
                        contentDescription = "Announcements",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Announcements",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.height(5.dp))
                announcementsLimitedData.value?.let {
                    if (it.size() > 0) {
                        it.forEach { announcement ->
                            val announcementData = announcement.toObject(AnnouncementList::class.java)
                            Log.d("MainScreen","announcementData $announcementData")
                            Row (
                                modifier = Modifier
                                    .padding(10.dp)
                                    .clickable {
                                        navController.navigate("AnnouncementDetailScreen/${announcementData.announcementID}")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                AsyncImage(
                                    model = if(announcementData.reporterProfileImageUrl.isBlank()) {
                                        R.drawable.account_circle_24
                                    } else {
                                        announcementData.reporterProfileImageUrl
                                    },
                                    contentDescription = "Reporter Profile Icon",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp)) // Rounded corners
                                        .size(40.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = announcementData.title.trimToLength(20),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = formatTimestamp(announcementData.date),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = announcementData.category,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            Spacer(Modifier.width(20.dp))
                        }
                        Spacer(Modifier.width(30.dp))
                        Button(
                            onClick = {
                                quickLinksData.value?.let {
                                    navController.navigate("AnnouncementsScreen")
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFADD8E6)
                            ),
                        ) {
                            Text(
                                text = "View More",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "No Data Found",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .fillMaxWidth(0.8f)
                    .height(310.dp)
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.report),
                        contentDescription = "Leave Report",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Leave Report",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.height(5.dp))
                leaveTypes.forEach { leaveType ->
                    Row (
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable {
                                val annualLeaveDataMap = Json.encodeToString(leaveTrackerDetails.value)
                                val encodedLeaveJson =
                                    URLEncoder.encode(
                                        annualLeaveDataMap,
                                        StandardCharsets.UTF_8.toString()
                                    )
                                navController.navigate("ApplyLeaveScreen/${encodedLeaveJson}/${leaveType}")
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            imageVector = leaveTypeIcons.getValue(leaveType),
                            contentDescription = "Leave type icon",
                            modifier = Modifier.size(25.dp),
                            tint = Color.Unspecified,
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = leaveType,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Taken : ${getPropertyValue(leaveTrackerDetails.value,leaveTypeDataClassMap.getValue("${leaveType}Booked"))} Day(s) | Balance : ${getPropertyValue(leaveTrackerDetails.value,leaveTypeDataClassMap.getValue("${leaveType}Balance"))} Day(s)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Spacer(Modifier.width(30.dp))
                Button(
                    onClick = {
                        quickLinksData.value?.let {
                            navController.navigate("LeaveReportScreen/${liveUserDetails.value.email}")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFADD8E6)
                    ),
                ) {
                    Text(
                        text = "View More",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .fillMaxWidth(0.8f)
                    .height(310.dp)
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.umbrella),
                        contentDescription = "Holidays",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Upcoming Holidays",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.height(5.dp))
                holidaysData.value?.take(3)?.forEach { holiday ->
                    val holidayData = holiday.toObject(HolidayData::class.java)
                    Row (
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Box(
                            modifier = Modifier
                                .size(35.dp)
                                .background(Color(0xFF0096FF), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                holidayData.initial,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(Modifier.width(15.dp))
                        Column {
                            Text(
                                text = holidayData.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = holidayFormatTimestamp(holidayData.date),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Spacer(Modifier.width(30.dp))
                Button(
                    onClick = {
                        quickLinksData.value?.let {
                            navController.navigate("UpcomingHolidaysScreen")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFADD8E6)
                    ),
                ) {
                    Text(
                        text = "View More",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(15.dp))
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                ClickableText(
                    text = AnnotatedString("Show Snackbar"),
                    onClick = {
                        //viewModel.toggleIsViewLoading()
                        viewModel.addUserToDB()
//                            viewModel.logoutCurrentUser()
                        scope.launch {
                            snackbarHostState.showSnackbar("Custom Snackbar!")
                        }
                    }
                )
            }
        }
    }

}

@Composable
fun SignInStatus(
    viewModel: MainScreenViewModel
){
    val userSignInStatus = appDataManager.liveUserSignInStatus.collectAsStateWithLifecycle()
    val userAttendanceData = viewModel.userAttendanceData.collectAsStateWithLifecycle()
    var elapsedSigninTime by remember { mutableStateOf(0L) }
    val (hours, minutes, seconds) = elapsedSigninTime.toHms()
    val parts = userAttendanceData.value.totalHours.toString().split('.')
    val totalHrsIntPart = parts[0]
    val totalHrsDecimalPart = if (parts.size > 1) parts[1] else "0"

    LaunchedEffect(Unit) {
        while (true) {
            elapsedSigninTime = getElapsedTime(userAttendanceData.value.checkInTime)
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .fillMaxWidth(0.8f)
            .background(Color.White)
            .padding(10.dp)
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (userSignInStatus.value == "Checked-In") {
                Text(
                    text = "$hours",
                    modifier = Modifier
                        .background(Color(0xFFFFD6D7))
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(text = " : ",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$minutes",
                    modifier = Modifier
                        .background(Color(0xFFFFD6D7))
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(text = " : ",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$seconds",
                    modifier = Modifier
                        .background(Color(0xFFFFD6D7))
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            } else {
                Text(
                    text = totalHrsIntPart,
                    modifier = Modifier
                        .background(Color(0xFFFFD6D7))
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(text = " : ",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = totalHrsDecimalPart,
                    modifier = Modifier
                        .background(Color(0xFFFFD6D7))
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(text = " : ",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "00",
                    modifier = Modifier
                        .background(Color(0xFFFFD6D7))
                        .padding(10.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            LinearProgressIndicator(
                progress = { hours.toFloat() / 9 },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color.Green,
                trackColor = Color.LightGray
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "General",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "09:00 AM To 06:00 PM",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(15.dp))
        Button(
            onClick = {
                viewModel.updateUserSignInStatus()
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                    if (userSignInStatus.value == "Checked-In") Color(0xFFD2042D)
                    else Color(0xFF0E7305),
            ),
        ) {
            Text(
                text = if (userSignInStatus.value == "Checked-In") "Check Out" else "Check In",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

fun String.trimToLength(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength) + "..."
    } else {
        this
    }
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd MMMM hh:mm a", Locale.getDefault())
    return formatter.format(date)
}

fun holidayFormatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)
    return formatter.format(date)
}

fun getElapsedTime(startTimeMillis: Long): Long {
    return System.currentTimeMillis() - startTimeMillis
}

fun Long.toHms(): Triple<Long,Long,Long> {
    val seconds = (this / 1000) % 60
    val minutes = (this / (1000 * 60)) % 60
    val hours = (this / (1000 * 60 * 60))
//    return Triple<"%02d:%02d:%02d", hours, minutes, seconds>
    return Triple(hours, minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskShowModalSheet(
    navController: NavController,
    viewModel: MainScreenViewModel
){
    var sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Column{
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.toggleAddTaskShowBottomSheet()
            },
            sheetState = sheetState,
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable{
                        viewModel.userEmailUiState?.let {
                            coroutineScope.launch {
                                sheetState.hide()
                                viewModel.toggleAddTaskShowBottomSheet()
                                if (!sheetState.isVisible) {
                                    navController.navigate("StatusScreen/${it}")
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.browser_window_icon),
                        contentDescription = "Status",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable{
                        viewModel.toggleAddTaskShowBottomSheet()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFF7F50), shape = RoundedCornerShape(15.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.forward_restore_icon),
                            contentDescription = "Comp Off",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Comp Off",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable{
                        viewModel.toggleAddTaskShowBottomSheet()
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.umbrella),
                        contentDescription = "Leave",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Leave",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable{
                        viewModel.toggleAddTaskShowBottomSheet()
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.pencil),
                        contentDescription = "Regularisation",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Regularisation",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.cancel_24dp),
                    contentDescription = "Cancel",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            viewModel.toggleAddTaskShowBottomSheet()
                        }
                )
            }
        }
    }

}


//"https://lh3.googleusercontent.com/a/ACg8ocJTGD7XPvLN7HGWvH7VBbssgR2EAWc5n7_7D5_6FbeZI__Zxeuk=s96-c",

/*
//    DisposableEffect(lifecycleOwner) {
//        lifecycleOwner.lifecycle.addObserver(viewModel)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(viewModel)
//        }
//    }

//    ModalNavigationDrawer(
//        drawerContent = {
//            Column(
//                Modifier
//                    .fillMaxHeight()
//                    .width(240.dp)
//                    .background(Color(0xFF1976D2))
//                    .padding(16.dp),
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text("Drawer Item 1", color = Color.White)
//                Spacer(Modifier.height(8.dp))
//                Text("Drawer Item 2", color = Color.White)
//            }
//        },
//        drawerState = drawerState
//    ) {

//            floatingActionButton = {
//                FloatingActionButton(
//                    onClick = {
//                        fabClicks++
//                        scope.launch {
//                            snackbarHostState.showSnackbar("FAB clicked $fabClicks times")
//                        }
//                    },
//                    containerColor = Color(0xFF1976D2)
//                ) {
//                    Icon(Icons.Default.Add, contentDescription = "Add")
//                }
//            },

 */