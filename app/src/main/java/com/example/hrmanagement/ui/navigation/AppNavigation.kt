package com.example.hrmanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.data.LeaveData
import com.example.hrmanagement.ui.announcement.AnnouncementDetailScreen
import com.example.hrmanagement.ui.announcement.AnnouncementsFilterScreen
import com.example.hrmanagement.ui.announcement.AnnouncementsScreen
import com.example.hrmanagement.ui.holiday.UpcomingHolidaysScreen
import com.example.hrmanagement.ui.leave.ApplyCompOffScreen
import com.example.hrmanagement.ui.main.MainScreen
import com.example.hrmanagement.ui.quickLink.QuickLinksScreen
import com.example.hrmanagement.ui.signin.FlashScreen
import com.example.hrmanagement.ui.signin.SignUpScreen
import com.example.hrmanagement.ui.leave.ApplyLeaveScreen
import com.example.hrmanagement.ui.userinfo.ColleagueInfoScreen
import com.example.hrmanagement.ui.leave.LeaveDetailsScreen
import com.example.hrmanagement.ui.leave.LeaveRegularisationScreen
import com.example.hrmanagement.ui.leave.LeaveReportScreen
import com.example.hrmanagement.ui.main.FavouritesScreen
import com.example.hrmanagement.ui.main.NotificationScreen
import com.example.hrmanagement.ui.main.StatusScreen
import com.example.hrmanagement.ui.more.FeedDetailScreen
import com.example.hrmanagement.ui.more.FeedsScreen
import com.example.hrmanagement.ui.more.SettingsScreen
import com.example.hrmanagement.ui.more.ThemeChangeScreen
import com.example.hrmanagement.ui.services.AttendanceComposableView
import com.example.hrmanagement.ui.services.AttendanceInformationScreen
import com.example.hrmanagement.ui.services.ColleaguesScreen
import com.example.hrmanagement.ui.services.EmployeeDetailsScreen
import com.example.hrmanagement.ui.services.EmployeeInformationScreen
import com.example.hrmanagement.ui.services.GoalsComposableView
import com.example.hrmanagement.ui.services.LeaveTrackerComposableView
import com.example.hrmanagement.ui.services.LeaveTrackerInformationScreen
import com.example.hrmanagement.ui.services.PerformanceInformationScreen
import com.example.hrmanagement.ui.userinfo.UserInfoScreen
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val token = runBlocking {
        MyApplication.appPreferenceDataStore.tokenFlow.firstOrNull()
    }

    NavHost(
        navController = navController,
        startDestination = if (token == null) {
            println("token - $token")
            "FlashScreen"
        } else {
            println("token - $token")
            "MainScreen"
        }
    ) {
        composable(route = "FlashScreen") {
            FlashScreen(modifier, navController)
        }
        composable(route = "SignUpScreen") {
            SignUpScreen(modifier, navController)
        }
        composable(route = "MainScreen") {
            MainScreen(modifier, navController)
        }

        composable(route = "ThemeChangeScreen") {
            ThemeChangeScreen(navController)
        }

        composable(
            route = "UserInfoScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null) {
                UserInfoScreen(modifier, navController, userEmailId)
            }
        }

        composable(
            route = "SettingsScreen"
        ) {
            SettingsScreen(modifier, navController)
        }

        composable(
            route = "FeedDetailScreen/{feedId}/{feedType}",
            arguments = listOf(
                navArgument("feedId") { type = NavType.IntType },
                navArgument("feedType") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val feedId = backStackEntry.arguments?.getInt("feedId")
            val feedType = backStackEntry.arguments?.getString("feedType")
            if ((feedType != null) && (feedId != null)) {
                FeedDetailScreen(modifier, navController, feedId, feedType)
            }
        }

        composable(
            route = "FeedsScreen"
        ) {
            FeedsScreen(modifier, navController)
        }

        composable(
            route = "FavouritesScreen"
        ) {
            FavouritesScreen(modifier, navController)
        }

        composable(
            route = "ColleagueInfoScreen/{colleagueEmailId}/{myEmailId}",
            arguments = listOf(
                navArgument("colleagueEmailId") { type = NavType.StringType },
                navArgument("myEmailId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val colleagueEmailId = backStackEntry.arguments?.getString("colleagueEmailId")
            val myEmailId = backStackEntry.arguments?.getString("myEmailId")
            if ((colleagueEmailId != null) && (myEmailId != null)) {
                ColleagueInfoScreen(modifier, navController, colleagueEmailId, myEmailId)
            }
        }

        composable(
            route = "LeaveDetailsScreen/{leaveData}",
            arguments = listOf(navArgument("leaveData") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedPersonJson = backStackEntry.arguments?.getString("leaveData")
            val leaveData = encodedPersonJson?.let {
                val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                Json.decodeFromString<LeaveData>(decodedJson)
            }
            if (leaveData != null) {
                LeaveDetailsScreen(modifier, navController, leaveData)
            }
        }

        composable(
            route = "ApplyLeaveScreen/{leaveType}",
            arguments = listOf(
                navArgument("leaveType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val leaveType = backStackEntry.arguments?.getString("leaveType")
            if (leaveType != null) {
                ApplyLeaveScreen(modifier, navController, leaveType)
            }
        }

        composable(
            route = "AttendanceInformationScreen"
        ) {
            AttendanceInformationScreen(navController)
        }

        composable(
            route = "LeaveTrackerInformationScreen"
        ) {
                LeaveTrackerInformationScreen(navController)
        }

        composable(route = "QuickLinksScreen") {
            QuickLinksScreen(modifier, navController)
        }

        composable(route = "GoalsComposableView") {
            GoalsComposableView(modifier, navController)
        }

        composable(
            route = "LeaveTrackerComposableView"
        ) {
                LeaveTrackerComposableView(navController)
        }

        composable(route = "AttendanceComposableView") {
            AttendanceComposableView(navController)
        }

        composable(route = "AnnouncementsScreen") {
            AnnouncementsScreen(modifier, navController)
        }

        composable(route = "AnnouncementsFilterScreen") {
            AnnouncementsFilterScreen(modifier, navController, listOf())
        }

        composable(route = "UpcomingHolidaysScreen") {
            UpcomingHolidaysScreen(modifier, navController)
        }

        composable(
            route = "LeaveRegularisationScreen"
        ) { backStackEntry ->
            LeaveRegularisationScreen(modifier, navController)
        }

        composable(
            route = "ColleaguesScreen"
        ) {
            ColleaguesScreen(navController)
        }

        composable(
            route = "EmployeeDetailsScreen"
        ) {
                EmployeeDetailsScreen(navController)
        }

        composable(
            route = "PerformanceInformationScreen"
        ) {
            PerformanceInformationScreen(navController)
        }

        composable(
            route = "NotificationScreen"
        ) {
            NotificationScreen(modifier, navController)
        }

        composable(
            route = "EmployeeInformationScreen"
        ) {
                EmployeeInformationScreen(modifier, navController)
        }

        composable(
            route = "ApplyCompOffScreen",
        ) { backStackEntry ->
            ApplyCompOffScreen(modifier, navController)
        }

        composable(
            route = "StatusScreen"
        ) {
            StatusScreen(modifier, navController)
        }

        composable(
            route = "LeaveReportScreen"
        ) { backStackEntry ->
            LeaveReportScreen(modifier, navController)
        }

        composable(
            route = "AnnouncementsFilterScreen/{filterData}",
            arguments = listOf(navArgument("filterData") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedLeaveJson = backStackEntry.arguments?.getString("filterData")
            val filterData = encodedLeaveJson?.let {
                val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                Json.decodeFromString<List<Map<String, Boolean>>>(decodedJson)
            }
            if (filterData != null) {
                AnnouncementsFilterScreen(modifier, navController, filterData)
            }
        }

        composable(
            route = "AnnouncementDetailScreen/{announcementId}",
            arguments = listOf(navArgument("announcementId") { type = NavType.IntType })
        ) { backStackEntry ->
            val announcementId = backStackEntry.arguments?.getInt("announcementId")
            if (announcementId != null) {
                AnnouncementDetailScreen(modifier, navController, announcementId)
            }
        }

    }
}


//            val userJson = backStackEntry.arguments?.getString("userLoginData")
//            val user = Json.decodeFromString<UserLoginData>(userJson ?: "")
//            UserInfoScreen(modifier,navController,user)
//        composable(route = "UserInfoScreen") {
//            UserInfoScreen(modifier,navController)
//       }
//        composable<UserLoginData> { backStackEntry ->
//            val profile: UserLoginData = backStackEntry.toRoute()
//            UserInfoScreen(profile)
//        }