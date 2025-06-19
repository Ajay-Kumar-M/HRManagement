package com.example.hrmanagement.ui.quickLink

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import com.example.hrmanagement.data.LinkData

@Composable
fun QuickLinksScreen(
    modifier: Modifier,
    navController: NavController,
    viewModel: QuickLinksViewModel = viewModel()
) {

    val quickLinksData = viewModel.quickLinksData.collectAsStateWithLifecycle()
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .background(Color.White)
                    .fillMaxWidth(),
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
                        contentDescription = "Previous screen"
                    )
                }
                Text("Leave Details",
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
//                                .verticalScroll(rememberScrollState())
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
                Spacer(Modifier.height(10.dp))
                quickLinksData.value?.let { it ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        items(it.documents.toList()) { quickLink ->
                            val linkData = quickLink.toObject(LinkData::class.java)
                            Row (
                                modifier = Modifier.padding(10.dp)
                            ){
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.link),
                                    contentDescription = "Links",
                                    tint = Color(0xFFADD8E6),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    buildAnnotatedString {
                                        withLink(
                                            LinkAnnotation.Url(
                                                url = linkData?.linkurl ?: "https://www.google.com",
                                                styles = TextLinkStyles(style = SpanStyle(color = Color.Blue))
                                            )
                                        ) {
                                            append(linkData?.linkname ?: "Link")
                                        }
                                    }
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                        }
                    }
                }
//
//                quickLinksData.value?.forEach { quickLink ->
//                    val linkData = quickLink.toObject(LinkData::class.java)
//                    Row (
//                        modifier = Modifier.padding(10.dp)
//                    ){
//                        Icon(
//                            imageVector = ImageVector.vectorResource(R.drawable.link),
//                            contentDescription = "Links",
//                            tint = Color(0xFFADD8E6),
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Spacer(Modifier.width(10.dp))
//                        Text(
//                            buildAnnotatedString {
//                                withLink(
//                                    LinkAnnotation.Url(
//                                        url = linkData.linkurl,
//                                        styles = TextLinkStyles(style = SpanStyle(color = Color.Blue))
//                                    )
//                                ) {
//                                    append(linkData.linkname)
//                                }
//                            }
//                        )
//                    }
//                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}