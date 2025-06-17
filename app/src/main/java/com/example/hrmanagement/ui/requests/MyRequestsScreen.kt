package com.example.hrmanagement.ui.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRequestsScreen(
    modifier: PaddingValues,
    navController: NavController,
    emailId: String,
    viewModel: MyRequestsViewModel = viewModel()
) {
    val tabs = listOf("Pending", "Approved", "Rejected")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(modifier)
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedTabIndex = index
                        when (selectedTabIndex) {
                            0 -> {
                            }
                            1 -> {

                            }
                            2 -> {

                            }
                        }
                    },
                    text = { Text(title) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> {
                PendingRequestsScreen(navController,emailId,viewModel)
            }
            1 -> {

            }
            2 -> {

            }
        }
    }
}

@Composable
fun PendingRequestsScreen(
    navController: NavController,
    emailId: String,
    viewModel: MyRequestsViewModel
){
    val isPendingViewLoading = viewModel.isPendingViewLoading.collectAsStateWithLifecycle()

    if (isPendingViewLoading.value) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicatorComposable()
        }
    } else {
        LazyColumn {

        }
    }
}