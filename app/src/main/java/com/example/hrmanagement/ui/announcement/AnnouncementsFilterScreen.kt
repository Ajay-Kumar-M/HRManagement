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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.component.CircularProgressIndicatorComposable

@Composable
fun AnnouncementsFilterScreen(
    modifier: Modifier,
    navController: NavController,
    filterData: List<Map<String, Boolean>>,
    viewModel: AnnouncementsFilterViewModel = viewModel()
){
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val categoryChildCheckedStates = viewModel.categoryChildCheckedStates.collectAsStateWithLifecycle()
    val categoryParentState = when {
        categoryChildCheckedStates.value.values.all { it } -> ToggleableState.On
        categoryChildCheckedStates.value.values.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }
    val locationChildCheckedStates = viewModel.locationChildCheckedStates.collectAsStateWithLifecycle()
    val locationParentState = when {
        locationChildCheckedStates.value.values.all { it } -> ToggleableState.On
        locationChildCheckedStates.value.values.none { it } -> ToggleableState.Off
        else -> ToggleableState.Indeterminate
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (filterData.isNotEmpty()) {
                        viewModel.toggleViewLoading()
                        filterData[0].forEach { index, value ->
                            viewModel.checkCategoryChildBox(index,value)
                        }
                        filterData[1].forEach { index, value ->
                            viewModel.checkLocationChildBox(index,value)
                        }
                        viewModel.toggleViewLoading()
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
                            contentDescription = "Announcement Screen"
                        )
                    }
                    Text(
                        "Filter By",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp, 5.dp)
                            .weight(1f,true)
                    )
                    Text(
                        "Reset",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(20.dp, 5.dp)
                        .clickable{
                            viewModel.resetAllCheckBox()
                        }
                    )
                    Spacer(Modifier.width(5.dp))
                }
            }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(15.dp))
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
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                        .weight(1f,true)
                ) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TriStateCheckbox(
                            state = categoryParentState,
                            onClick = {
                                val newState = categoryParentState != ToggleableState.On
                                categoryChildCheckedStates.value.forEach { index,_ ->
                                    viewModel.checkCategoryChildBox(index, newState)
                                }
                            }
                        )
                        Text("Select all categories")
                    }
                    categoryChildCheckedStates.value.keys.forEach{ key ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(25.dp,0.dp,0.dp,0.dp)
                                .height(30.dp)
                        ) {
                            Checkbox(
                                checked = categoryChildCheckedStates.value.getValue(key),
                                onCheckedChange = { isChecked ->
                                    // Update the individual child state
                                    viewModel.checkCategoryChildBox(key, isChecked)
                                }
                            )
                            Text(key) //categoryMap.getValue(index))
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TriStateCheckbox(
                            state = locationParentState,
                            onClick = {
                                // Determine new state based on current state
                                val newState = locationParentState != ToggleableState.On
                                locationChildCheckedStates.value.forEach { index, _ ->
                                    //categoryChildCheckedStates.value[index] = newState
                                    viewModel.checkLocationChildBox(index, newState)
                                }
                            }
                        )
                        Text("Select all locations")
                    }
                    // Child Checkboxes
                    locationChildCheckedStates.value.keys.forEach { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(25.dp,0.dp,0.dp,0.dp)
                                .height(30.dp)
                        ) {
                            Checkbox(
                                checked = locationChildCheckedStates.value.getValue(index),
                                onCheckedChange = { isChecked ->
                                    // Update the individual child state
                                    viewModel.checkLocationChildBox(index, isChecked)
                                }
                            )
                            Text(index)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilledIconButton(
                        onClick = {
                            navController.previousBackStackEntry?.savedStateHandle
                                ?.set("filters",listOf(categoryChildCheckedStates.value,locationChildCheckedStates.value))
                            navController.popBackStack()
                        },
                        shape = RoundedCornerShape(16.dp), // Apply rounded corners
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFF097969),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(40.dp)
                    ) {
                        Text(
                            "Apply",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}