package com.example.hrmanagement.ui.userinfo

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hrmanagement.R
import com.example.hrmanagement.Service.MyApplication.Companion.appDataManager
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.AttendanceData
import com.example.hrmanagement.data.GoalData
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.data.UserLoginData
import com.example.hrmanagement.ui.main.UserProfileImage
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Calendar
import java.util.Locale
import kotlin.reflect.full.memberProperties

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    modifier: Modifier,
    navController: NavController,
    emailId: String,
    viewModel: UserInfoScreenViewModel = viewModel()
) {
    val userImageUri = viewModel.userImageUriFlowState.collectAsStateWithLifecycle()
    val userLoginData = viewModel.userLoginData.collectAsStateWithLifecycle()
    val userSignInStatus = appDataManager.liveUserSignInStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val tabs = listOf("Profile", "Team", "Leave Tracker", "Goals", "Attendance")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(visible = selectedTabIndex == 2 || selectedTabIndex == 3) {
                ExtendedFloatingActionButton(
                    text = { Text("Add") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                    onClick = {
                        when(selectedTabIndex) {
                            2 -> {
                                navController.navigate("ApplyLeaveScreen/${userLoginData.value.email}/All")
                            }
                            3 -> {

                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) //.verticalScroll(rememberScrollState())
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                    )
                    {
                        if (userImageUri.value?.isBlank() == true) {
                            Image(
                                painter = rememberVectorPainter(Icons.Filled.AccountCircle),
                                alpha = 0.5f,
                                contentDescription = null,
                                modifier = Modifier.matchParentSize()
                            )
                        } else {
                            AsyncImage(
                                model = userImageUri.value,
                                contentDescription = "Profile Icon",
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.FillBounds,
                                placeholder = rememberVectorPainter(ImageVector.vectorResource(R.drawable.account_placeholder))
                            )
                        }
                        IconButton(
                            onClick = { navController.popBackStack() },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White, // Your desired background color
                                contentColor = Color.Black          // Icon color
                            ),
                            modifier = Modifier
                                .size(60.dp)
                                .padding(15.dp)// Custom size
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Favorite"
                            )
                        }
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, userLoginData.value.profileUrl)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share URL via")
                                context.startActivity(shareIntent)
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .size(60.dp)
                                .padding(15.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Profile"
                            )
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = (-16).dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, // Transparent
                            ),
                        ) {
                            Text(
                                text = userSignInStatus.value,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black,
                                modifier = Modifier
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            }

            stickyHeader(
                contentType = "sticky"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = userLoginData.value.username.toString(),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        ),
                        color = Color.Black,
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = userLoginData.value.email.toString(),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default
                        ),
                        color = Color.Black,
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 3.dp
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    selectedTabIndex = index
                                    when (selectedTabIndex) {
                                        1 -> {
                                            if (userLoginData.value.departmentName.isNotBlank()) {
                                                viewModel.getDepartmentDetails(userLoginData.value.departmentName)
                                            }
                                        }
                                    }
                                },
                                text = { Text(title) }
                            )
                        }
                    }
                }
            }
            if (isViewLoading.value) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicatorComposable()
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        when (selectedTabIndex) {
                            0 -> {
                                ProfileComposable(userLoginData.value,navController)
                            }
                            1 -> {
                                TeamComposable(viewModel, navController)
                            }
                            2 -> {
                                LeaveTrackerComposable(navController)
                            }
                            3 -> {
                                GoalsComposable()
                            }
                            4 -> {
                                AttendanceComposable()
                            }
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun AttendanceComposable(
    viewModel: AttendanceViewModel = viewModel()
){
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val attendanceFilterShowBottomSheet = viewModel.attendanceFilterShowBottomSheet.collectAsStateWithLifecycle()
    val attendanceMonthShowModal = viewModel.attendanceMonthShowModal.collectAsStateWithLifecycle()
    val attendanceSelectedViewType = viewModel.attendanceSelectedViewType.collectAsStateWithLifecycle()
    val attendanceTotalHours = viewModel.attendanceTotalHours.collectAsStateWithLifecycle()
    val attendanceStartDate = viewModel.attendanceStartDate.collectAsStateWithLifecycle()
    val attendanceEndDate = viewModel.attendanceEndDate.collectAsStateWithLifecycle()
    val attendanceDataQuerySnapshot = viewModel.attendanceDataQuerySnapshot.collectAsStateWithLifecycle()
    val attendanceDayOfTheWeekIndex = viewModel.attendanceDayOfTheWeekIndex.collectAsStateWithLifecycle()
    val attendanceSelectedYear = viewModel.attendanceSelectedYear.collectAsStateWithLifecycle()
    val attendanceSelectedMonth = viewModel.attendanceSelectedMonth.collectAsStateWithLifecycle()
    val attendanceModalSelectedDate = viewModel.attendanceModalSelectedDate.collectAsStateWithLifecycle()
    val userSignInStatus = appDataManager.liveUserSignInStatus.collectAsStateWithLifecycle()
    val leaveTypeColorMap: Map<String, Color> = mapOf(
        Pair("Casual Leave",Color(0xFFE08607)),
        Pair("Sick Leave",Color(0xFFE08607)),
        Pair("On Duty",Color(0xFFE08607)),
        Pair("Optional Holidays",Color(0xFFE08607)),
        Pair("Comp Off",Color(0xFFE08607)),
        Pair("Casual Leave",Color(0xFFE08607)),
        Pair("Casual Leave",Color(0xFFE08607)),
        Pair("Holidays",Color.Blue),
        Pair("",Color.Green),
        Pair("null",Color.Green)
    )
    val monthMap: Map<Int, String> = mapOf(
        Pair(1,"Jan"),Pair(2,"Feb"),Pair(3,"Mar"),Pair(4,"Arp"),Pair(5,"May"),Pair(6,"Jun"),Pair(7,"Jul"),Pair(8,"Aug"),Pair(9,"Sept"),Pair(10,"Oct"),Pair(11,"Nov"),Pair(12,"Dec")
    )
    val dotRadius = 8f

    if (isViewLoading.value) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicatorComposable()
        }
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
        ) {
            IconButton(
                onClick = {
                    viewModel.toggleAttendanceFilterShowBottomSheet()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .width(110.dp)
                    .height(35.dp)
                    .padding(3.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "${attendanceSelectedViewType.value}  ",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .width(60.dp)
                            .height(25.dp)
//                    .align(Alignment.Center)
                            .background(Color.White, shape = RoundedCornerShape(16.dp)),
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.filter_list_24dp),
                        contentDescription = "Change attendance view"
                    )
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            if (attendanceSelectedViewType.value=="Week") {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.White)
                        .clip(RoundedCornerShape(25.dp))
                        .fillMaxWidth()
                        .padding(5.dp),
                    //verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            viewModel.decrementAttendanceWeek()
                            viewModel.getAttendanceDetails()
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp)
//                    .background(Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Year"
                        )
                    }
                    Text(
                        "${attendanceStartDate.value} to ${attendanceEndDate.value}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(5.dp)
                    )
                    IconButton(
                        onClick = {
                            viewModel.incrementAttendanceWeek()
                            viewModel.getAttendanceDetails()
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
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Year"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                    attendanceDataQuerySnapshot.value?.forEach { attendanceLog ->
                        val attendanceData = attendanceLog.toObject(AttendanceData::class.java)
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = attendanceData.date
                        val dayOfTheWeek = calendar.getDisplayName(
                            Calendar.DAY_OF_WEEK,
                            Calendar.SHORT,
                            Locale.getDefault()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dayOfTheWeek ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    text = "${attendanceData.friendlyDateValue}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            when (attendanceData.leaveType) {
                                in listOf(
                                    "Casual Leave",
                                    "Sick Leave",
                                    "On Duty",
                                    "Optional Holidays",
                                    "Comp Off"
                                ) -> {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(18.dp, 0.dp)
                                    ) {
                                        Text(
                                            text = attendanceData.leaveType,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                        LinearProgressIndicator(
                                            progress = { 1f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp),
                                            color = Color(0xFFFFAC1C),
                                            trackColor = Color.LightGray,
                                            strokeCap = StrokeCap.Round
                                        )
                                    }
                                }

                                "Holidays" -> {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(18.dp, 0.dp)
                                    ) {
                                        Text(
                                            text = attendanceData.leaveType,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                        LinearProgressIndicator(
                                            progress = { 1f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp),
                                            color = Color.Blue,
                                            trackColor = Color.LightGray
                                        )
                                    }
                                }

                                "" -> {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(15.dp, 0.dp)
                                    ) {
                                        if (((dayOfTheWeek == "Sat") || (dayOfTheWeek == "Sun")) && (attendanceData.checkInTime <= 0L)) {
                                            Text(
                                                text = "Weekend",
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                            LinearProgressIndicator(
                                                progress = { 1f },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(4.dp),
                                                color = Color(0xFF800020),
                                                trackColor = Color.LightGray
                                            )
                                        } else {
                                            Text(
                                                text = attendanceData.leaveType,
                                                style = MaterialTheme.typography.bodySmall,
                                            )
                                            Row {
                                                Canvas(
                                                    modifier = Modifier
                                                        .size((dotRadius * 2).dp)
                                                        .offset(x = 6.dp, y = (-6).dp)
                                                ) {
                                                    drawCircle(
                                                        color = if (attendanceData.status == "Present") Color.Green else Color.Red,
                                                        radius = dotRadius
                                                    )
                                                }
                                                LinearProgressIndicator(
                                                    progress = { attendanceData.totalHours.toFloat() / 9 },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(4.dp),
                                                    color = if (attendanceData.status == "Present") Color.Green else Color.Red,
                                                    trackColor = Color.LightGray
                                                )
                                                Canvas(
                                                    modifier = Modifier
                                                        .size((dotRadius * 2).dp)
                                                        .offset(x = (-1).dp, y = (-6).dp)
                                                ) {
                                                    drawCircle(
                                                        color = Color.Red,
                                                        radius = dotRadius
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Hrs",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    text = "${attendanceData.totalHours}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.background(Color.White)
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            viewModel.decrementAttendanceMonth()
                            viewModel.fillAttendanceCalendarMetadata()
                            viewModel.getAttendanceDetails()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous month"
                            )
                        }
                        Text(
                            text = "${monthMap.getValue(attendanceSelectedMonth.value)} ${attendanceSelectedYear.value}",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        )
                        IconButton(onClick = {
                            viewModel.incrementAttendanceMonth()
                            viewModel.fillAttendanceCalendarMetadata()
                            viewModel.getAttendanceDetails()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next month"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        viewModel.daysOfWeek.forEach { day ->
                            Text(
                                text = day,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    var tempDayOfTheWeekIndex = attendanceDayOfTheWeekIndex.value
                    var currentAttendanceDataIndex = 0
                    var attendanceDataEndIndex = attendanceDataQuerySnapshot.value?.size() ?: 0
                    if (attendanceDataEndIndex == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Data",
                                style = MaterialTheme.typography.titleLarge,
                            )

                        }
                    } else {
                        repeat(6) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(7) {
                                    if (tempDayOfTheWeekIndex > 0) {
                                        Text("         ")
                                        tempDayOfTheWeekIndex--
                                    } else if(currentAttendanceDataIndex < attendanceDataEndIndex){
                                        val documentSnapshot = attendanceDataQuerySnapshot.value?.elementAt(currentAttendanceDataIndex)
                                        val attendanceData = documentSnapshot?.toObject(AttendanceData::class.java)
                                        attendanceData?.let {
                                            Box(
                                                modifier = Modifier
//                                            .background(
//                                                color = if (attendanceModalSelectedDate.value == attendanceData?.friendlyDateValue) {
//                                                    MaterialTheme.colorScheme.secondaryContainer
//                                                } else {
//                                                    Color.Transparent
//                                                }
//                                            )
                                                    .clickable {
                                                        viewModel.changeAttendanceMonthSelectedDate(
                                                            attendanceData.friendlyDateValue
                                                        )
                                                        viewModel.toggleAttendanceMonthShowModal()
                                                    }
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = "${attendanceData.friendlyDateValue}",
                                                        style = MaterialTheme.typography.titleSmall,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Row(
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Canvas(
                                                            modifier = Modifier
                                                                .size((5f * 2).dp)
//                                                          .offset(x = (-1).dp)
                                                        ) {
                                                            drawCircle(
                                                                color = if (attendanceData.checkInTime > 0) {
                                                                    Color.Green
                                                                } else if (attendanceData.leaveType.isNotBlank()) {
                                                                    leaveTypeColorMap.getValue((attendanceData.leaveType.toString()))
                                                                } else {
                                                                    Color(0xFF800020)
                                                                },
                                                                radius = 5f
                                                            )
                                                        }
                                                        Text(
                                                            text = "${attendanceData.totalHours}",
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        currentAttendanceDataIndex++
                                    } else {
                                        Text("         ")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                }
            }
            Button(
                onClick = {
                    viewModel.updateUserSignInStatus()
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0E7305),
                ),
            ) {
                Text(
                    text = if (userSignInStatus.value=="Checked-In") "Check Out" else "Check In",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
//                modifier = Modifier
//                    .background(
//                        color = Color(0xFF10570A),
//                        shape = RoundedCornerShape(20.dp)
//                    )
//                    .padding(10.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(Color.White)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.clock_24dp),
                    contentDescription = "Clock"
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "Total Hours ${"%.2f".format(attendanceTotalHours.value)} HRS",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black,
                )
            }
            if (attendanceFilterShowBottomSheet.value) AttendanceFilterShowModalSheet(viewModel)
            if (attendanceMonthShowModal.value) AttendanceMonthInfoModal(attendanceDataQuerySnapshot.value,attendanceModalSelectedDate.value,attendanceSelectedYear.value,attendanceSelectedMonth.value,viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceMonthInfoModal(
    attendanceDataQuerySnapshot: QuerySnapshot?,
    attendanceModalSelectedDate: Int,
    attendanceSelectedYear: Int,
    attendanceSelectedMonth: Int,
    viewModel: AttendanceViewModel
){
    val sheetState = rememberModalBottomSheetState()
    val documentSnapshot = attendanceDataQuerySnapshot?.elementAt(attendanceModalSelectedDate-1)
    val attendanceData = documentSnapshot?.toObject(AttendanceData::class.java)

    Column{
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.toggleAttendanceMonthShowModal()
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center
            ) {
                attendanceData?.let { attendanceLog ->
                    Text(
                        "${attendanceData.friendlyDateValue}-${attendanceData.friendlyMonthValue}-${attendanceData.friendlyYearValue}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    if (attendanceLog.checkInTime > 0) {
                        Text(
                            attendanceData.status,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                                .padding(5.dp)
                                .background(shape = RoundedCornerShape(16.dp), color = Color(0xFFE1E3E3))
                        ) {
                            Column {
                                Text(
                                    "Check-In",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    "${attendanceData.friendlyDateValue}-${attendanceData.friendlyMonthValue}-${attendanceData.friendlyYearValue}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = attendanceData.checkInTime
                                Text(
                                    "${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)} ${
                                        if (calendar.get(
                                                Calendar.AM_PM
                                            ) == 0
                                        ) {
                                            "AM"
                                        } else {
                                            "PM"
                                        }
                                    }",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    attendanceData.checkInLocation,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column {
                                Text(
                                    "Check-Out",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    "${attendanceData.friendlyDateValue}-${attendanceData.friendlyMonthValue}-${attendanceData.friendlyYearValue}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = attendanceData.checkOutTime
                                Text(
                                    "${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)} ${
                                        if (calendar.get(
                                                Calendar.AM_PM
                                            ) == 0
                                        ) {
                                            "AM"
                                        } else {
                                            "PM"
                                        }
                                    }",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    attendanceData.checkInLocation,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else if (attendanceLog.leaveType.isNotBlank()) {
                        Text(
                            attendanceData.leaveType,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else if(
                        (viewModel.getDayOfWeekOfMonth(attendanceSelectedYear,attendanceSelectedMonth,attendanceModalSelectedDate) == "Sun")||
                        (viewModel.getDayOfWeekOfMonth(attendanceSelectedYear,attendanceSelectedMonth,attendanceModalSelectedDate) == "Sat")
                        )
                    {
                        Text(
                            "Weekend",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Text(
                            "Absent",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceFilterShowModalSheet(
    viewModel: AttendanceViewModel
){
    val sheetState = rememberModalBottomSheetState()
    val selectedViewType = viewModel.attendanceSelectedViewType.collectAsStateWithLifecycle()
    val viewTypes = listOf("Week","Month")

    Column{
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.toggleAttendanceFilterShowBottomSheet()
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                viewTypes.forEach { viewValue ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                if (selectedViewType.value == "Week") {
                                    viewModel.fillAttendanceCalendarMetadata()
                                    viewModel.changeAttendanceViewType(viewValue)
                                    viewModel.toggleAttendanceFilterShowBottomSheet()
                                    viewModel.getAttendanceDetails()
                                } else {
                                    viewModel.getCurrentWeekRangeUsingCalendar()
                                    viewModel.changeAttendanceViewType(viewValue)
                                    viewModel.toggleAttendanceFilterShowBottomSheet()
                                    viewModel.getAttendanceDetails()
                                }

                            }
                    ) {
                        if (selectedViewType.value==viewValue){
                            Icon(
                                imageVector = Icons.Default.Check, //ImageVector.vectorResource(R.drawable.filter_list_24dp),
                                contentDescription = "Change attendance view"
                            )
                        } else {
                            Spacer(modifier = Modifier.width(25.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            viewValue,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

}

@Composable
fun GoalsComposable(viewModel: GoalsViewModel = viewModel())
{
    val goalsList = listOf("Low", "Medium", "High", "Highest", "None")
    val goalTypeColor: Map<String, Color> = mapOf(
        Pair("Low", Color.Green),
        Pair("Medium", Color(0xFFFFA500)),
        Pair("High", Color.Blue),
        Pair("Highest", Color.Red),
        Pair("None", Color.Gray),
    )
    val goalsQuerySnapshot = viewModel.goalsQuerySnapshot.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        Row(modifier = Modifier
            .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Goals",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(10.dp)
                    .width(70.dp)
                    .height(30.dp)
//                    .align(Alignment.Center)
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                textAlign = TextAlign.Center
            )
//            IconButton(
//                onClick = {
//
//                },
//                colors = IconButtonDefaults.iconButtonColors(
//                    containerColor = Color.White,
//                    contentColor = Color.Black
//                ),
//                modifier = Modifier
//                    .size(60.dp)
//                    .padding(15.dp)
//            ) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(R.drawable.filter_list_24dp),
//                    contentDescription = "Share Profile"
//                )
//            }
        }
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            goalsList.forEach { goal ->
                Text(
                    text = "  $goal",
                    modifier = Modifier
                        .padding(start = 3.dp, top = 10.dp, bottom = 10.dp, end = 3.dp)
                        .drawBehind {
                            drawLine(
                                color = goalTypeColor.getValue(goal),
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 10f
                            )
                        },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        goalsQuerySnapshot.value?.forEach { document ->
            val goal = document.toObject(GoalData::class.java)
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(20.dp)
                    .fillMaxWidth()
                    .clickable {
//                        val userJson = Json.encodeToString(allLeaves.value)
//                        val encodedUserJson =
//                            URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
//                        navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                    }
            ) {
                Row(
                    modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Text(
                            goal.goalName,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(5.dp)
                        )
                        Canvas(
                            modifier = Modifier
                                .size((10f * 2).dp)
                                .offset(x = (-1).dp)
                        ) {
                            drawCircle(
                                color = goalTypeColor.getValue(goal.priority), //Color.Red,
                                radius = 10f
                            )
                        }
                    }
                    if (goal.comments.isNotEmpty()) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.mark_unread_chat_alt_24dp),
                            contentDescription = "Goal Comment available"
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.comment_24dp),
                            contentDescription = "Goal Comment unavilable"
                        )
                    }
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    if (goal.progress<100) {
                        Text(
                            "In-Progress",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(5.dp)
                        )
                    } else {
                        Text(
                            "Completed",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${goal.progress}%",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(5.dp)
                        )
                        LinearProgressIndicator(
                        progress = { goal.progress.toFloat() / 100 },
                            modifier = Modifier
                                .width(70.dp)
                                .height(10.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveTrackerComposable(
    navController: NavController,
    viewModel: LeaveTrackerViewModel = viewModel()
){
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val leaveRequests = viewModel.leaveRequests.collectAsStateWithLifecycle()
    val calendarYear = viewModel.calendarYear.collectAsStateWithLifecycle()
    val leaveTypes = listOf("Casual Leave", "Sick Leave", "On Duty", "Optional Holidays", "Comp Off")
    val leaveTypeIcons: Map<String, ImageVector> = mapOf(
        Pair("Casual Leave",ImageVector.vectorResource(id = R.drawable.calendar_mark)),
        Pair("Sick Leave",ImageVector.vectorResource(id = R.drawable.local_hospital_24dp)),
        Pair("On Duty",ImageVector.vectorResource(id = R.drawable.assignment_ind_24dp)),
        Pair("Optional Holidays",ImageVector.vectorResource(id = R.drawable.beach_access_24dp)),
        Pair("Comp Off",ImageVector.vectorResource(id = R.drawable.event_24dp)))
    val leaveTypeDataClassMap: Map<String, String> = mapOf(
        Pair("Casual LeaveBooked","casualLeaveBooked"),
        Pair("Casual LeaveBalance","casualLeaveBalance"),
        Pair("Sick LeaveBooked","sickLeaveBooked"),
        Pair("Sick LeaveBalance","sickLeaveBalance"),
        Pair("On DutyBooked","onDutyLeaveBooked"),
        Pair("On DutyBalance","onDutyLeaveBalance"),
        Pair("Optional HolidaysBooked","optionalLeaveBooked"),
        Pair("Optional HolidaysBalance","optionalLeaveBalance"),
        Pair("Comp OffBooked","compOffLeaveBooked"),
        Pair("Comp OffBalance","compOffLeaveBalance"))
    val leaveTrackerData = viewModel.liveLeaveTrackerDetails.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.getLeaveTrackerDetails()
                    viewModel.getLeaveRequests()
//                    viewModel.getGoals()
//                    viewModel.getAttendanceDetails()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (isViewLoading.value) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicatorComposable()
        }
    } else {
        Column(modifier = Modifier
            .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White)
                    .clip(RoundedCornerShape(25.dp))
                    .fillMaxWidth()
                    .padding(5.dp),
                //verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        viewModel.decrementCalendarYear()
                        viewModel.getLeaveTrackerDetails()
                        viewModel.getLeaveRequests()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
//                    .background(Color.Red)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Year"
                    )
                }
                Text(
                    "01 Jan ${calendarYear.value} to 31 Dec ${calendarYear.value}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(5.dp)
                )
                IconButton(
                    onClick = {
                        viewModel.incrementCalendarYear()
                        viewModel.getLeaveTrackerDetails()
                        viewModel.getLeaveRequests()
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
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Year"
                    )
                }
            }
            Row(
                Modifier.horizontalScroll(rememberScrollState())
            ) {
                leaveTypes.forEach { leaveType ->
                    Column(
                        modifier = Modifier
                            .background(Color.White)
                            .clip(RoundedCornerShape(16.dp))
                            .padding(16.dp)
                            .clickable {
                                viewModel.changeShowBottomSheetLeaveType(leaveType)
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = leaveTypeIcons.getValue(leaveType), //Icons.Default.Share,
                                contentDescription = "Leave type icon",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                leaveType,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(5.dp, 5.dp, 5.dp, 0.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(
                                    getPropertyValue(
                                        leaveTrackerData.value,
                                        leaveTypeDataClassMap.getValue("${leaveType}Balance")
                                    ).toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Balance", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Column {
                                Text(
                                    getPropertyValue(
                                        leaveTrackerData.value,
                                        leaveTypeDataClassMap.getValue("${leaveType}Booked")
                                    ).toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Booked", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    LeaveTrackerShowModalSheet(leaveTypeDataClassMap,sheetState, viewModel)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            leaveRequests.value.forEach { leaveRequest ->
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clickable {
                            val userJson = Json.encodeToString(leaveRequest)
                            val encodedUserJson =
                                URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("LeaveDetailsScreen/${encodedUserJson}")
                        }
                ) {
                    Text(
                        "${leaveRequest.leaveType} - ${leaveRequest.numberOfDays} Day(s)",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        "${leaveRequest.fromDateString} To ${leaveRequest.toDateString}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        leaveRequest.status,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(5.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

        }
    }
}

@Composable
fun TeamComposable(
    viewModel: UserInfoScreenViewModel,
    navController: NavController
){
    val departmentDetails = viewModel.liveDepartmentDetails.collectAsStateWithLifecycle()
    val userLoginData = viewModel.userLoginData.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Team Name", style = MaterialTheme.typography.titleMedium)
            Text(
                modifier = Modifier
                    .padding(5.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color.White,
                            radius = this.size.minDimension
                        )
                    },
                textAlign = TextAlign.End,
                color = Color.Black,
                text = "${departmentDetails.value?.size() ?: 0}",
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        departmentDetails.value?.forEach { person ->
            val teamMemberInfo = person.toObject(UserLoginData::class.java)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("ColleagueInfoScreen/${teamMemberInfo.email}/${userLoginData.value.email}")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserProfileImage(teamMemberInfo.imageUrl)
                Column (
                    modifier = Modifier.padding(30.dp,0.dp,5.dp,0.dp)
                )
                {
                    Text(
                        teamMemberInfo.username,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.id_card_svg),
                            contentDescription = "ID",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            teamMemberInfo.emp_Id,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveTrackerShowModalSheet(
    leaveTypeDataClassMap: Map<String,String>,
    sheetState: SheetState,
    viewModel: LeaveTrackerViewModel
){
    val coroutineScope = rememberCoroutineScope()
    val leaveTrackerData = viewModel.liveLeaveTrackerDetails.collectAsStateWithLifecycle()
    val leaveTrackerShowBottomSheet = viewModel.leaveTrackerShowBottomSheet.collectAsStateWithLifecycle()

    if (leaveTrackerShowBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    sheetState.hide() // animate out
                    viewModel.toggleLeaveTrackerShowBottomSheet() // update state after animation
                }
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    viewModel.showBottomSheetLeaveType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        "Balance",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        getPropertyValue(
                            leaveTrackerData.value,
                            leaveTypeDataClassMap.getValue("${viewModel.showBottomSheetLeaveType}Balance")
                        ).toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        "Booked",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        getPropertyValue(
                            leaveTrackerData.value,
                            leaveTypeDataClassMap.getValue("${viewModel.showBottomSheetLeaveType}Booked")
                        ).toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileComposable(
    userLoginData: UserLoginData,
    navController: NavController
)
{
    Column {
        Text("About",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Designation",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text("Employee", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            "Employee ID",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            userLoginData.emp_Id,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Department",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(userLoginData.departmentName, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Mobile Number",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            userLoginData.mobileNumber,
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            "Seating Location",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text("Block 1", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            "Reporting To",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("ColleagueInfoScreen/${userLoginData.reportingTo.getValue("emailId")}/${userLoginData.email}")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfileImage(userLoginData.reportingTo.getValue("imageUrl"))
            Column (
                modifier = Modifier.padding(30.dp,0.dp,5.dp,0.dp)
            )
            {
                Text(
                    userLoginData.reportingTo.getValue("username"),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    userLoginData.reportingTo.getValue("designation"),
                    style = MaterialTheme.typography.bodySmall
                )
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.id_card_svg),
                        contentDescription = "ID",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        userLoginData.reportingTo.getValue("employeeId"),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        getAllFieldsAndValues(userLoginData).forEach { value ->
            if ((value.first != "token")&&(value.first != "reportingTo")) {
                Text(
                    value.first,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    "${value.second ?: ""}",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

fun getPropertyValue(instance: Any, propertyName: String): Any? {
    val property = instance::class.memberProperties.find { it.name == propertyName }
    return property?.getter?.call(instance)
}

/*

//@Composable
//fun ContentItem(
//    date: CalendarUiState.Date,
//    onClickListener: (CalendarUiState.Date) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .background(
//                color = if (date.isSelected) {
//                    MaterialTheme.colorScheme.secondaryContainer
//                } else {
//                    Color.Transparent
//                }
//            )
//            .clickable {
//                onClickListener(date)
//            }
//    ) {
//        Text(
//            text = date.dayOfMonth,
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier
//                .align(Alignment.Center)
//                .padding(10.dp)
//        )
//    }
//}

                repeat(6) {
                    if (index >= dates.size) return@repeat
                    Row {

                        repeat(7) {
                            val item = if (index < dates.size) dates[index] else CalendarUiState.Date.Empty
                            ContentItem(
                                date = item,
                                onClickListener = {
                                },
                                modifier = Modifier.weight(1f)
                            )
                            index++
                        }
                    }
                }

import kotlin.reflect.full.memberProperties

fun <T> getPropertyValue(instance: Any, propertyName: String): T? {
    val property = instance::class.memberProperties.find { it.name == propertyName }
    return property?.getter?.call(instance) as? T
}


//                Button(onClick = {
//                    scope.launch { sheetState.hide() }.invokeOnCompletion {
//                        if (!sheetState.isVisible) {
//                            showBottomSheet = false
//                        }
//                    }
//                }) {
//                    Text("Hide bottom sheet")
//                }

DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    // Handle ON_CREATE event
                    Log.d("UserInfoScreen","Lifecycle.Event.ON_CREATE")
                }
                Lifecycle.Event.ON_START -> {
                    // Handle ON_START event
                    Log.d("UserInfoScreen","Lifecycle.Event.ON_start")
                    viewModel.getLeaveTrackerDetails()
                }
                Lifecycle.Event.ON_RESUME -> {
                    // Handle ON_RESUME event
                    Log.d("UserInfoScreen","Lifecycle.Event.ON_RESUME")
                }
                Lifecycle.Event.ON_PAUSE -> {
                    // Handle ON_PAUSE event
                    Log.d("UserInfoScreen","Lifecycle.Event.ON_PAUSE")
                }
                Lifecycle.Event.ON_STOP -> {
                    // Handle ON_STOP event
                    Log.d("UserInfoScreen","Lifecycle.Event.ON_STOP")
                }
                Lifecycle.Event.ON_DESTROY -> {
                    // Handle ON_DESTROY event
                    Log.d("UserInfoScreen","Lifecycle.Event.ON_DESTROY")
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

 */