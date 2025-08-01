package com.example.hrmanagement.ui.services

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.component.truncate
import com.example.hrmanagement.ui.main.UserProfileImage
import javax.annotation.meta.When

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColleaguesScreen(
    navController: NavController,
    viewModel: ColleaguesViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val isViewTypeList = viewModel.isViewTypeList.collectAsStateWithLifecycle()
    val colleagueSearchText = viewModel.colleagueSearchText.collectAsStateWithLifecycle()
    val viewTabs = listOf("Favourites","Colleagues","Departments")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(10.dp,0.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
//                    colors = IconButtonDefaults.iconButtonColors(
//                        containerColor = Color.White,
//                        contentColor = Color.Black
//                    ),
                    modifier = Modifier
                        .size(40.dp)
                        .weight(0.15f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous page"
                    )
                }
                TextField(
                    value = colleagueSearchText.value,
                    onValueChange = {
                        viewModel.colleagueSearchTextChanged(it)
                    },
                    singleLine = true,
                    placeholder = { Text("Search ${viewTabs[selectedTabIndex]}", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(25.dp))
                        .padding(horizontal = 5.dp)
                        .height(50.dp)
                        .weight(0.85f),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(fontSize = 14.sp),
                    trailingIcon = {
                        if (colleagueSearchText.value.isNotEmpty()){
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.clickable{
                                    viewModel.colleagueSearchTextChanged("")
                                }
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if(selectedTabIndex==1) {
                FloatingActionButton(
                    onClick = {
                        viewModel.toggleIsViewType()
                    },
                    containerColor = Color(0xFF1976D2)
                ) {
                    if (isViewTypeList.value) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.list),
                            contentDescription = "List",
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            ImageVector.vectorResource(R.drawable.grid),
                            contentDescription = "Grid",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    PrimaryScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 3.dp,
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        viewTabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedTabIndex = index
                                    when (selectedTabIndex) {
                                        0 -> {
                                            viewModel.fetchFavorites()
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
                            FavoritesListView(viewModel,viewModel.appUserData.email,navController)
                        }
                        1 -> {
                            ColleaguesGridView(viewModel,viewModel.appUserData.email,navController)
                        }
                        2 -> {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("No Data Available")
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun FavoritesListView(
    viewModel: ColleaguesViewModel,
    emailId: String,
    navController: NavController
){

    val favouritesData = viewModel.favouritesData.collectAsStateWithLifecycle()

    if (favouritesData.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(favouritesData.value) { favoriteUserData ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(15.dp,10.dp)
                        .clickable{
                            if (favoriteUserData.email == emailId) {
                                navController.navigate("UserInfoScreen")
                            } else {
                                navController.navigate("ColleagueInfoScreen/${favoriteUserData.email}/${emailId}")
                            }
                        }
                ) {
                    UserProfileImage(favoriteUserData.imageUrl)
                    Spacer(Modifier.width(15.dp))
                    Column{
                        Text(
                            favoriteUserData.username.truncate(15),
                            fontSize = 12.sp,
                        )
                        Text(
                            favoriteUserData.employeeId,
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No Data Available")
        }
    }
}

@Composable
fun ColleaguesGridView(
    viewModel: ColleaguesViewModel,
    emailId: String,
    navController: NavController
){
    val filteredUsersData = viewModel.filteredUsersData.collectAsStateWithLifecycle()
    val isViewTypeList = viewModel.isViewTypeList.collectAsStateWithLifecycle()

    if (isViewTypeList.value) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
//                    modifier = Modifier.padding(30.dp),
//            contentPadding = PaddingValues(20.dp),
//            verticalArrangement = Arrangement.spacedBy(10.dp),
//            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsersData.value) { userData ->
                Box(
                    modifier = Modifier.fillMaxSize()
                        .background(Color.White, RoundedCornerShape(20.dp))
//                        .padding(20.dp)
                        .height(140.dp)
                        .clickable {
                            if (userData.email == emailId) {
                                navController.navigate("UserInfoScreen")
                            } else {
                                navController.navigate("ColleagueInfoScreen/${userData.email}/${emailId}")
                            }
                        }
                ) {
                    if (userData.imageUrl.isBlank()) {
                        Image(
                            painter = rememberVectorPainter(Icons.Filled.AccountCircle),
                            alpha = 0.5f,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(userData.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            placeholder = rememberVectorPainter(ImageVector.vectorResource(R.drawable.account_placeholder)),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                    Column (
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            userData.username.truncate(15),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            userData.departmentName.truncate(15),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredUsersData.value) { userData ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(15.dp,10.dp)
                        .clickable{
                            if (userData.email == emailId) {
                                navController.navigate("UserInfoScreen")
                            } else {
                                navController.navigate("ColleagueInfoScreen/${userData.email}/${emailId}")
                            }
                        }
                ) {
                    UserProfileImage(userData.imageUrl)
                    Spacer(Modifier.width(15.dp))
                    Column{
                        Text(
                            userData.username.truncate(15),
                            fontSize = 12.sp,
                        )
                        Text(
                            userData.departmentName.truncate(15),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}