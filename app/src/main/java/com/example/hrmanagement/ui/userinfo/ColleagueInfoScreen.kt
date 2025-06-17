package com.example.hrmanagement.ui.userinfo

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.UserLoginData
import com.example.hrmanagement.ui.main.UserProfileImage
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ColleagueInfoScreen(
    modifier: Modifier,
    navController: NavController,
    colleagueEmailId: String,
    myEmailId: String,
    viewModel: ColleagueInfoScreenViewModel = viewModel()
) {
    val departmentDetails = viewModel.liveDepartmentDetails.collectAsStateWithLifecycle()
    val liveColleagueDetails = viewModel.liveColleagueDetails.collectAsStateWithLifecycle()
    val liveUserFavoriteDetails = viewModel.liveUserFavoriteDetails.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val tabs = listOf("Profile", "Team")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { /* Handle result if needed */ }

    LaunchedEffect(viewModel) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
//                    viewModel.getLeaveTrackerDetails()
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

    LazyColumn(
        modifier = modifier.fillMaxSize() //.verticalScroll(rememberScrollState())
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth())
                {
                    if (liveColleagueDetails.value.imageUrl.isBlank()) {
                        Image(
                            painter = rememberVectorPainter(Icons.Filled.AccountCircle),
                            alpha = 0.5f,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        AsyncImage(
                            model = liveColleagueDetails.value.imageUrl,
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
                        modifier = Modifier.size(60.dp)
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
                                putExtra(Intent.EXTRA_TEXT,
                                    liveColleagueDetails.value.profileUrl
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share URL via")
                            try {
                                context.startActivity(shareIntent)
                            } catch (e: Exception) {
                                // Handle exception if no email client is installed
                                e.printStackTrace()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(60.dp)
                            .padding(15.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Profile"
                        )
                    }
                    val isFavorite = liveUserFavoriteDetails.value.any { it.email == colleagueEmailId }
                    IconButton(
                        onClick = {
                            if (isFavorite) {
                                viewModel.removeColleagueFromFavorites()
                            } else {
                                viewModel.addColleagueToFavorites()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(60.dp)
                            .padding(15.dp)
                            .align(Alignment.TopEnd)
                            .offset(y=40.dp)
                    ) {
                        if (isFavorite){
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.star_filled),
                                contentDescription = "Favorite",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.star_outlined),
                                contentDescription = "Not Favorite",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_INSERT).apply {
                                type = ContactsContract.RawContacts.CONTENT_TYPE
                                putExtra(ContactsContract.Intents.Insert.NAME, liveColleagueDetails.value.username)
                                putExtra(ContactsContract.Intents.Insert.PHONE, liveColleagueDetails.value.mobileNumber)
                                putExtra(ContactsContract.Intents.Insert.EMAIL, liveColleagueDetails.value.email)
                                putExtra(ContactsContract.Intents.Insert.COMPANY, liveColleagueDetails.value.departmentName)
                            }
                            val packageManager = context.packageManager
                            if (intent.resolveActivity(packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "No contacts app found.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = ContactsContract.Contacts.CONTENT_URI
                                }
                                context.startActivity(intent)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.size(60.dp)
                            .padding(15.dp)
                            .align(Alignment.TopEnd)
                            .offset(y=80.dp)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.add_contact_person),
                            contentDescription = "Add to contacts",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    }
//                    Text(
//                        text = userSignInStatus.value,
//                        style = TextStyle(
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = FontFamily.Default
//                        ),
//                        color = Color.Black,
//                        modifier = Modifier
//                            .background(
//                                color = Color.White,
//                                shape = RoundedCornerShape(10.dp)
//                            )
//                            .padding(10.dp)
//                            .align(Alignment.BottomCenter)
//                    )
                }
            }
        }

        stickyHeader(
            contentType = "sticky"
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = liveColleagueDetails.value.username,
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
                    text = liveColleagueDetails.value.email,
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
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.White),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = "Send Mail",
                        modifier = Modifier.padding(15.dp,5.dp,15.dp,5.dp)
                            .clickable{
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:${liveColleagueDetails.value.email}".toUri()
//                                    putExtra(Intent.EXTRA_SUBJECT, subject)
//                                    putExtra(Intent.EXTRA_TEXT, body)
                                }
                                try {
                                    context.startActivity(Intent.createChooser(intent, "Send Email"))
                                } catch (e: Exception) {
                                    // Handle exception if no email client is installed
                                    e.printStackTrace()
                                }
                            }
                    )
                    Image(
                        painter = painterResource(R.drawable.chat_24dp),
                        contentDescription = "Chat",
                        modifier = Modifier.padding(15.dp,5.dp,15.dp,5.dp)
                            .clickable{

                            }
                    )
//                    Icon(
//                        imageVector = Icons.Default.Search,
//                        contentDescription = "Search",
//                        modifier = Modifier.padding(5.dp)
//                            .clickable{
//
//                            }
//                    )

                }
                PrimaryScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                if (selectedTabIndex == 1) {
                                    viewModel.getColleagueDepartmentMembers(liveColleagueDetails.value.departmentName)
                                }
                            },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
        if (isViewLoading.value){
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
//                            Column {
//                                Text("About", style = MaterialTheme.typography.titleLarge)
//                                Text(
//                                    "Designation",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
//                                )
//                                Text("Employee", style = MaterialTheme.typography.titleSmall)
//                                Text(
//                                    "Employee ID",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
//                                )
//                                Text(
//                                    colleagueData.emp_Id,
//                                    style = MaterialTheme.typography.titleSmall
//                                )
//                                Text(
//                                    "Department",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
//                                )
//                                Text("Department Name", style = MaterialTheme.typography.titleSmall)
//                                Text(
//                                    "Mobile Number",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
//                                )
//                                Text(
//                                    colleagueData.mobileNumber,
//                                    style = MaterialTheme.typography.titleSmall
//                                )
//                                Text(
//                                    "Seating Location",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
//                                )
//                                Text("Block", style = MaterialTheme.typography.titleSmall)
//                            }

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
                                    liveColleagueDetails.value.emp_Id,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(15.dp))

                                Text(
                                    "Department",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(liveColleagueDetails.value.departmentName, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.height(15.dp))

                                Text(
                                    "Mobile Number",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    liveColleagueDetails.value.mobileNumber,
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
                                            navController.navigate("ColleagueInfoScreen/${liveColleagueDetails.value.reportingTo.getValue("emailId")}/$myEmailId")
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    UserProfileImage(liveColleagueDetails.value.reportingTo.getValue("imageUrl"))
                                    Column (modifier = Modifier.padding(30.dp,0.dp,5.dp,0.dp))
                                    {
                                        Text(
                                            liveColleagueDetails.value.reportingTo.getValue("username"),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            liveColleagueDetails.value.reportingTo.getValue("designation"),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Row {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.id_card_svg),
                                                contentDescription = "ID",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                liveColleagueDetails.value.reportingTo.getValue("employeeId"),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                    }
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                                getAllFieldsAndValues(liveColleagueDetails.value).forEach { value ->
                                    if (value.first != "token") {
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

                        1 -> {
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
                                departmentDetails.value?.forEach { person ->
                                    val teamMemberInfo = person.toObject(UserLoginData::class.java)
                                    Row(
                                        modifier = Modifier.clickable{
                                            val userJson = Json.encodeToString(person)
                                            val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                                            navController.navigate("ColleagueInfoScreen/${teamMemberInfo.email}/$myEmailId")
                                        }
                                    ) {
                                        UserProfileImage(teamMemberInfo.imageUrl)
                                        Column (modifier = Modifier.padding(30.dp,0.dp,5.dp,0.dp)){
                                            Text(
                                                teamMemberInfo.username,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Row {
                                                Icon(
                                                    imageVector = Icons.Default.AccountBox,
                                                    contentDescription = "ID"
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
                    }
                }
            }
        }
    }
}