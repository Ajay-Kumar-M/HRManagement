package com.example.hrmanagement.ui.services

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun EmployeeInformationScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: EmployeeInformationViewModel = viewModel()
) {
    val listOfActions: List<Pair<String,String>> = listOf(
        "Colleagues" to "ColleaguesScreen",
        "Employee Details" to "EmployeeDetailsScreen",
    )

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
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
                        contentDescription = "Next Year"
                    )
                }
                Text("Employee Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp,5.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(15.dp))
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            listOfActions.forEach { action ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .padding(15.dp)
                        .clickable{
                            when(action.first) {
                                "Employee Details","Colleagues" -> {
                                    navController.navigate(action.second)
                                }
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "List",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                            .background(Color(0xFFFF7F50),RoundedCornerShape(15.dp))
                            .padding(5.dp)
                    )
                    Spacer(Modifier.width(15.dp))
                    Text(
                        action.first,
//                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}