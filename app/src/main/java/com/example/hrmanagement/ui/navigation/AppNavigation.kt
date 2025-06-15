package com.example.hrmanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hrmanagement.Service.MyApplication
import com.example.hrmanagement.data.LeaveTrackerData
import com.example.hrmanagement.data.UserLoginData
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
import com.example.hrmanagement.ui.main.StatusScreen
import com.example.hrmanagement.ui.services.ColleaguesScreen
import com.example.hrmanagement.ui.services.EmployeeDetailsScreen
import com.example.hrmanagement.ui.services.EmployeeInformationScreen
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
        startDestination = if(token == null) "FlashScreen" else "MainScreen"
    ) {
        composable(route = "FlashScreen") {
            FlashScreen(modifier,navController)
        }
        composable(route = "SignUpScreen") {
            SignUpScreen(modifier,navController)
        }
        composable(route = "MainScreen") {
            MainScreen(modifier,navController)
        }

        composable(
            route = "UserInfoScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null) {
                UserInfoScreen(modifier,navController,userEmailId)
            }
        }

        composable (
            route = "ColleagueInfoScreen/{personEmailId}",
            arguments = listOf(navArgument("personEmailId") { type = NavType.StringType })
        ){ backStackEntry ->
            val person = backStackEntry.arguments?.getString("personEmailId")
            if (person != null) {
                ColleagueInfoScreen(modifier,navController,person)
            }
        }

        composable (
            route = "LeaveDetailsScreen/{leaveData}",
            arguments = listOf(navArgument("leaveData") { type = NavType.StringType })
        ){ backStackEntry ->
            val encodedPersonJson = backStackEntry.arguments?.getString("leaveData")
            val leaveData = encodedPersonJson?.let {
                val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                Json.decodeFromString<Map<String,String>>(decodedJson)
            }
            if (leaveData != null) {
                LeaveDetailsScreen(modifier,navController,leaveData)
            }
        }

        composable (
            route = "ApplyLeaveScreen/{leaveTrackerData}/{leaveType}",
            arguments = listOf(
                navArgument("leaveTrackerData") { type = NavType.StringType },
                navArgument("leaveType") { type = NavType.StringType }
                )
        ){ backStackEntry ->
            val leaveType = backStackEntry.arguments?.getString("leaveType")
            val encodedLeaveJson = backStackEntry.arguments?.getString("leaveTrackerData")
            val leaveData = encodedLeaveJson?.let {
                val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                Json.decodeFromString<LeaveTrackerData>(decodedJson)
            }
            if ((leaveData != null) && (leaveType != null)) {
                ApplyLeaveScreen(modifier,navController, leaveData, leaveType)
            }
        }

        composable(route = "QuickLinksScreen") {
            QuickLinksScreen(modifier,navController)
        }

        composable(route = "AnnouncementsScreen") {
            AnnouncementsScreen(modifier,navController)
        }

        composable(route = "AnnouncementsFilterScreen") {
            AnnouncementsFilterScreen(modifier,navController,listOf())
        }

        composable(route = "UpcomingHolidaysScreen") {
            UpcomingHolidaysScreen(modifier,navController)
        }

        composable(
            route = "LeaveRegularisationScreen/{userEmailId}/{username}/{employeeId}",
            arguments = listOf(
                navArgument("userEmailId") { type = NavType.StringType },
                navArgument("username") { type = NavType.StringType },
                navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            val username = backStackEntry.arguments?.getString("username")
            val employeeId = backStackEntry.arguments?.getString("employeeId")
            if (userEmailId != null)
                LeaveRegularisationScreen(modifier,navController, userEmailId,username, employeeId)
        }

        composable(
            route = "ColleaguesScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null)
                ColleaguesScreen(navController, userEmailId)
        }

        composable(
            route = "EmployeeDetailsScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null)
                EmployeeDetailsScreen(navController, userEmailId)
        }

        composable(
            route = "EmployeeInformationScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null)
                EmployeeInformationScreen(modifier,navController, userEmailId)
        }

        composable(
            route = "ApplyCompOffScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null)
                ApplyCompOffScreen(modifier,navController, userEmailId)
        }

        composable(
            route = "StatusScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null)
                StatusScreen(modifier,navController, userEmailId)
        }

        composable(
            route = "LeaveReportScreen/{userEmailId}",
            arguments = listOf(navArgument("userEmailId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userEmailId = backStackEntry.arguments?.getString("userEmailId")
            if (userEmailId != null)
            LeaveReportScreen(modifier,navController, userEmailId)
        }

        composable (
            route = "AnnouncementsFilterScreen/{filterData}",
            arguments = listOf(navArgument("filterData") { type = NavType.StringType })
        ){ backStackEntry ->
            val encodedLeaveJson = backStackEntry.arguments?.getString("filterData")
            val filterData = encodedLeaveJson?.let {
                val decodedJson = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
                Json.decodeFromString<List<Map<String, Boolean>>>(decodedJson)
            }
            if (filterData != null) {
                AnnouncementsFilterScreen(modifier,navController, filterData)
            }
        }

        composable (
            route = "AnnouncementDetailScreen/{announcementId}",
            arguments = listOf(navArgument("announcementId") { type = NavType.IntType })
        ){ backStackEntry ->
            val announcementId = backStackEntry.arguments?.getInt("announcementId")
            if (announcementId != null) {
                AnnouncementDetailScreen(modifier,navController,announcementId)
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