package com.example.hrmanagement.ui.more

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.MoreItemData
import com.example.hrmanagement.data.UserLoginData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    modifierPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: MoreViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val isPullDownRefreshing = remember { mutableStateOf(false) }
    val userLoginData = viewModel.appUserData
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val expandedItemId = remember { mutableStateOf<Int?>(null) }
    val viewList: List<MoreItemData> = listOf(
        MoreItemData(1,"Feeds",R.drawable.feeds_icon,"FeedsScreen",mapOf()),
        MoreItemData(2,"Employee Information",R.drawable.employee_information,"",
            mapOf("Colleagues" to "ColleaguesScreen","Employee Details" to "EmployeeDetailsScreen")),
        MoreItemData(3,"Attendance",R.drawable.attendance,"",
            mapOf("View" to "AttendanceComposableView","Regularization" to "LeaveRegularisationScreen")),
        MoreItemData(4,"Performance",R.drawable.performance,"",
            mapOf("Goals" to "GoalsComposableView")),
        MoreItemData(5,"Announcements",R.drawable.announcement,"AnnouncementsScreen",mapOf()),
        MoreItemData(6,"Leave Tracker",R.drawable.leave_tracker,"",
            mapOf("View" to "LeaveTrackerComposableView","Compensatory Request" to "ApplyCompOffScreen","Holidays" to "UpcomingHolidaysScreen")),
        MoreItemData(7,"Time Tracker",R.drawable.time_tracker,"",mapOf()),
        MoreItemData(8,"Tasks",R.drawable.tasks,"",mapOf()),
        MoreItemData(9,"Cases",R.drawable.cases,"",mapOf()),
    )
    val EXPANSION_TRANSITION_DURATION = 300

    PullToRefreshBox(
        isRefreshing = isPullDownRefreshing.value,
        onRefresh = {
            //Get MoreItemData from network
        },
        modifier = Modifier.fillMaxSize()
    ) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize().padding(modifierPaddingValues)
            ) {
                LazyColumn {
                    items(viewList) { parent ->
                        if (parent.subItems.isNotEmpty()) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expandedItemId.value = if (expandedItemId.value == parent.id) {
                                                null // Collapse if already expanded
                                            } else {
                                                parent.id // Expand this item, collapse others
                                            }
                                        }
                                        .padding(20.dp)
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(parent.icon),
                                        contentDescription = "Icon",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(25.dp)
                                    )
                                    Spacer(Modifier.width(15.dp))
                                    Text(
                                        parent.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (expandedItemId.value == parent.id){
                                        Icon(
                                            imageVector = Icons.Outlined.KeyboardArrowUp,
                                            contentDescription = "Shrink dropdrown",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Outlined.KeyboardArrowDown,
                                            contentDescription = "Expand dropdrown",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }
                                }
                                parent.subItems.forEach { subItem ->
                                        AnimatedVisibility(
                                            visible = expandedItemId.value == parent.id,
                                            enter = expandVertically(
                                                expandFrom = Alignment.Top,
                                                animationSpec = tween(EXPANSION_TRANSITION_DURATION)
                                            ) + fadeIn(
                                                initialAlpha = 0.3f,
                                                animationSpec = tween(EXPANSION_TRANSITION_DURATION)
                                            ),
                                            exit = shrinkVertically(
                                                shrinkTowards = Alignment.Top,
                                                animationSpec = tween(EXPANSION_TRANSITION_DURATION)
                                            ) + fadeOut(
                                                animationSpec = tween(EXPANSION_TRANSITION_DURATION)
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(
                                                    start = 60.dp,
                                                    top = 10.dp,
                                                    bottom = 10.dp
                                                )
                                                .clickable {
                                                    if (subItem.value.isNotBlank()) {
                                                        navController.navigate(subItem.value)
                                                    }
                                                }
                                        ) {
                                            Text(
                                                text = subItem.key,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                            }
                        } else {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (parent.navigationLink.isNotBlank()){
                                                navController.navigate(parent.navigationLink)
                                            }
                                        }
                                        .padding(20.dp)
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(parent.icon),
                                        contentDescription = "List",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(25.dp)
                                    )
                                    Spacer(Modifier.width(15.dp))
                                    Text(
                                        parent.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                                Spacer(Modifier.height(5.dp))
                            }
                        }
                    }
                }


            }
        }
    }
}