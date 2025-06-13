package com.example.hrmanagement.ui.main

import android.R.attr.text
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hrmanagement.R
import com.example.hrmanagement.component.CircularProgressIndicatorComposable
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StatusScreen(
    modifier: Modifier,
    navController: NavController,
    userEmailID: String,
    viewModel: StatusViewModel = viewModel()
) {
    val allUsersData = viewModel.allUsersData.collectAsStateWithLifecycle()
    val filteredUsersData = viewModel.filteredUsersData.collectAsStateWithLifecycle()
    val isSearchDropdownExpanded = viewModel.isSearchDropdownExpanded.collectAsStateWithLifecycle()
    val isViewLoading = viewModel.isViewLoading.collectAsStateWithLifecycle()
    val isSuccessDialogVisible = viewModel.isSuccessDialogVisible.collectAsStateWithLifecycle()
    val statusData = viewModel.statusData.collectAsStateWithLifecycle()
    val dollorMapData = viewModel.dollorMapData.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    var cursorOffset by remember { mutableStateOf(Offset.Zero) }
    var cursorHeight by remember { mutableStateOf(0f) }
    val navigateFeed = remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var showExitDialog by remember { mutableStateOf(false) }

    if (navigateFeed.value && !isSuccessDialogVisible.value) {
        LaunchedEffect(Unit) {
            navController.navigate("MainScreen")
            navigateFeed.value = false // Reset flag
        }
    }

    BackHandler(enabled = true) {
        if (statusData.value.text.isEmpty()){
            navController.popBackStack()
        } else {
            showExitDialog = true
        }
    }

    Scaffold(
        modifier = Modifier.padding(5.dp),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .background(Color.White)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            if (statusData.value.text.isEmpty()){
                                navController.popBackStack()
                            } else {
                                showExitDialog = true
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Home",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Text("Status",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(20.dp,0.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(10.dp,0.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Update Status",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                focusManager.clearFocus()
                                viewModel.addUserStatus(userEmailID)
                            }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(15.dp))
                .fillMaxSize()
                .background(Color.White)
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
                        .padding(10.dp)
                        .fillMaxSize(),
                ) {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color(0xFFE0E0E0))
                            .padding(5.dp)
                    ) {
//                        var textState by remember { mutableStateOf(statusData.value.text) }
//                        var indexMap by remember { mutableStateOf(dollorMapData.value.toMutableMap()) }
                        // Find all $ positions
                        fun getDollarIndices(text: String): List<Int> =
                            text.indices.filter { text[it] == '$' }

                        // Visual transformation: Replace $ with mapped unit visually
                        val visualTransformation = VisualTransformation { text ->
                            val builder = AnnotatedString.Builder()
                            var lastIndex = 0
                            var offset = 0
                            val dollarIndices = getDollarIndices(text.text)
                            dollarIndices.forEachIndexed { i, idx ->
                                builder.append(text.text.substring(lastIndex, idx))
                                val mapped = dollorMapData.value[idx]
                                if (mapped != null) {
                                    builder.pushStringAnnotation(tag = "unit", annotation = mapped)
                                    builder.withStyle(
                                        SpanStyle(color = Color.Blue,
                                        fontWeight = FontWeight.Bold)
                                    ) {
                                        builder.append(mapped)
                                    }
                                    builder.pop()
                                    offset += mapped.length - 1
                                } else {
                                    builder.append("$")
                                }
                                lastIndex = idx + 1
                            }
                            builder.append(text.text.substring(lastIndex))
                            println("statusscreen builder 2 - ${builder.toAnnotatedString()}")
                            TransformedText(builder.toAnnotatedString(), DollarReplacementOffsetMapping(text.text,dollorMapData.value))
                        }

                        fun handleValueChange(newValue: TextFieldValue) {
//                            val oldText = textState
                            val newText = newValue.text
                            // Detect which mapped unit was removed by comparing old and new text
                            val removedIndices = dollorMapData.value.keys.filter { it >= newText.length || newText[it] != '$' }
                            removedIndices.forEach { viewModel.removeDollorMap(it) }
//                            textState = newText
                        }



                        BasicTextField(
                            visualTransformation = visualTransformation,
                            value = statusData.value,
//                                TextFieldValue(
//                                text = statusData.value.text,
//                                selection = TextRange(textState.length) // Cursor at end
//                            ), //TextFieldValue(textState) , //statusData.value,//textFieldValue,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Black
                            ),
                            onValueChange = { changedText ->
                                handleValueChange(changedText)
                                viewModel.onStatusChange(changedText)
                                val cursorOffsetNew = changedText.selection.start
                                val lines = changedText.text.lines()
                                val cursorLine = run {
                                    var total = 0
                                    for (line in lines) {
                                        val nextTotal = total + line.length + 1 // +1 for \n
                                        if (cursorOffsetNew <= nextTotal) return@run line
                                        total = nextTotal
                                    }
                                    lines.lastOrNull() ?: ""
                                }
                                val searchString = cursorLine.substringAfter("@","")
                                if (searchString.isNotBlank()){
                                    viewModel.filterUsers(searchString)
                                } else {
                                    if (filteredUsersData.value.isNotEmpty()) {
                                        viewModel.clearFilteredUserData()
                                        if (isSearchDropdownExpanded.value){
                                            viewModel.toggleSearchDropdown()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFE0E0E0))
                                .focusRequester(focusRequester),
                            onTextLayout = { textLayoutResult ->
                                val cursorRect = textLayoutResult.getCursorRect(statusData.value.selection.start)
                                cursorOffset = cursorRect.bottomLeft
                                cursorHeight = cursorRect.height
                            },
                        )
                        if (statusData.value.text.isEmpty()) {
                            Text(
                                text = "Type @ to mention someone",
                                modifier = Modifier.align(Alignment.TopStart),
                                fontSize = 16.sp
                            )
                        }
                    }

                    val maxVisibleItems = minOf(3,filteredUsersData.value.size)
                    val menuPadding = with(density) { ((maxVisibleItems) * 130).toDp().toPx() }
                    val dpOffset = with(density) {
                        DpOffset(
                            (cursorOffset.x.toDp() + 10.dp),
                            (cursorOffset.y + cursorHeight + menuPadding).toDp()
                        )
                    }

                    DropdownMenu(
                        expanded = isSearchDropdownExpanded.value,
                        onDismissRequest = { viewModel.toggleSearchDropdown() },
                        offset = dpOffset,
                        properties = PopupProperties(focusable = false),
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .heightIn(max = 58.dp * maxVisibleItems)
                    ) {
                        filteredUsersData.value.forEach { userData ->
                            DropdownMenuItem(
                                text =
                                    {
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            AsyncImage(
                                                model = if(userData.imageUrl.isBlank()) {
                                                    R.drawable.account_circle_24
                                                } else {
                                                    userData.imageUrl
                                                },
                                                placeholder = painterResource(R.drawable.account_circle_24),
                                                contentDescription = "Profile Icon",
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(userData.username)
                                        }
                                    },
                                onClick = {
                                    viewModel.onMentionSelection(userData)
                                    viewModel.toggleSearchDropdown()
                                }
                            )
                        }
                    }
                    Spacer(Modifier.width(20.dp))
                }

            }
            if (isSuccessDialogVisible.value) {
                AlertDialog(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(16.dp)),
                    icon = {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Example Icon",
                            tint = Color(0xFF097969),
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    title = {
                        Text(text = "Success")
                    },
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Post added Successfully")
                        }
                    },
                    onDismissRequest = {
                        viewModel.toggleSuccessDialog()
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.toggleSuccessDialog()
                                navigateFeed.value=true
                            },
                            modifier = Modifier
                                .background(Color.Black, shape = RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text(
                                text = "View Status",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
//                    dismissButton = {
//                        TextButton(
//                            onClick = {
//                                viewModel.toggleSuccessDialog()
//                            }
//                        ) {
//                            Text("Dismiss")
//                        }
//                    }
                )
            }

            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = { Text("Confirm Navigation") },
                    text = { Text("Are you sure you want to go back?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                            navController.popBackStack()
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

class DollarReplacementOffsetMapping(
    private val original: String,
    private val replacementMap: Map<Int, String>
) : OffsetMapping {
    // Precompute the positions and lengths
    private val replacements = replacementMap.entries.sortedBy { it.key }

    override fun originalToTransformed(offset: Int): Int {
        var transformedOffset = offset
        for ((index, replacement) in replacements) {
            if (index < offset) {
                // Each replacement adds (replacement.length - 1) chars
                transformedOffset += replacement.length - 1
            } else {
                break
            }
        }
        return transformedOffset
    }

    override fun transformedToOriginal(offset: Int): Int {
        var originalOffset = offset
        for ((index, replacement) in replacements) {
            val transformedIndex = index + (replacement.length - 1) * replacements.count { it.key < index }
            if (transformedIndex < offset) {
                originalOffset -= replacement.length - 1
            } else {
                break
            }
        }
        // Clamp to valid range
        return originalOffset.coerceIn(0, original.length)
    }
}

@Composable
fun DropdownBelowCursorInBasicTextField() {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var expanded by remember { mutableStateOf(false) }
    var cursorOffset by remember { mutableStateOf(Offset.Zero) }
    var cursorHeight by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    ){
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                textFieldValue = it
                expanded = true
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0E0E0)),
            onTextLayout = { textLayoutResult ->
                val cursorRect = textLayoutResult.getCursorRect(textFieldValue.selection.start)
                cursorOffset = cursorRect.bottomLeft
                cursorHeight = cursorRect.height
            }
        )

        val menuPadding = with(density) { 100.dp.toPx() }
        // Convert cursorOffset from pixels to dp and add cursorHeight to y for below-cursor placement
        val dpOffset = with(density) {
            DpOffset(
                (cursorOffset.x.toDp() + 5.dp),
                (cursorOffset.y + cursorHeight + menuPadding).toDp()
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = dpOffset,
            properties = PopupProperties(focusable = false)
        ) {
            DropdownMenuItem(
                text = { Text("Option 1") },
                onClick = { /* Handle click */ }
            )
            DropdownMenuItem(
                text = { Text("Option 2") },
                onClick = { /* Handle click */ }
            )
        }
    }

}

@Composable
fun DropdownNearCursorInBasicTextField() {
//    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var expanded by remember { mutableStateOf(false) }
    var cursorOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    BasicTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            expanded = true
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFE0E0E0)),
        onTextLayout = { textLayoutResult ->
            val cursorRect = textLayoutResult.getCursorRect(textFieldValue.selection.start)
            cursorOffset = cursorRect.bottomLeft
        }
    )

    // Convert cursorOffset from pixels to dp
    val dpOffset = with(density) { DpOffset(cursorOffset.x.toDp(), cursorOffset.y.toDp()) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        offset = dpOffset,
        properties = PopupProperties(focusable = false)
    ) {
        DropdownMenuItem(
            text = { androidx.compose.material3.Text("Option 1") },
            onClick = { /* Handle click */ }
        )
        DropdownMenuItem(
            text = { androidx.compose.material3.Text("Option 2") },
            onClick = { /* Handle click */ }
        )
    }
}

@Composable
fun MultiLineTextFieldWithCursorDropdown() {
    var text by remember { mutableStateOf("") }
    var showDropdown by remember { mutableStateOf(false) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var textFieldCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {

        // TextField with layout info
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                showDropdown = true
            },
            modifier = Modifier
                .padding(16.dp)
                .onGloballyPositioned { coords -> textFieldCoordinates = coords }
                .background(Color(0xFFE0E0E0))
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            textStyle = TextStyle(fontSize = 16.sp, lineHeight = 20.sp),
            onTextLayout = { layoutResult -> textLayoutResult = layoutResult }
        )

        // Cursor offset logic
        val cursorOffsetInWindow: Offset? = remember(text, textLayoutResult, textFieldCoordinates) {
            val cursorPosition = text.length.coerceAtLeast(0)
            val layout = textLayoutResult ?: return@remember null
            val coordinates = textFieldCoordinates ?: return@remember null
            try {
                val cursorRect = layout.getCursorRect(cursorPosition)
                coordinates.localToWindow(cursorRect.bottomLeft)
            } catch (e: Exception) {
                null
            }
        }

        // Convert offset to DpOffset
        val dropdownOffset = with(density) {
            cursorOffsetInWindow?.let {
                DpOffset(it.x.toDp(), it.y.toDp())
            } ?: DpOffset(0.dp, 0.dp)
        }

        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false },
            offset = dropdownOffset,
            properties = PopupProperties(focusable = false) // Keeps TextField focused
        ) {
            DropdownMenuItem(
                text =
                    {
                        Text("userData1")

                        Spacer(modifier = Modifier.height(10.dp))
                    },
                onClick = {
                    showDropdown = false
//                    viewModel.onMentionSelection(userData)
//                    viewModel.toggleSearchDropdown()
                }
            )
            DropdownMenuItem(
                text =
                    {
                        Text("userData2")

                        Spacer(modifier = Modifier.height(10.dp))
                    },
                onClick = {
                    showDropdown = false
//                    viewModel.onMentionSelection(userData)
//                    viewModel.toggleSearchDropdown()
                }
            )
        }
    }
}

/*

//                LaunchedEffect(Unit) {
//                    delay(1000)
//                    focusRequester.requestFocus()
//                }

                        val inputTransformation = object : InputTransformation {
                            override fun TextFieldBuffer.transformInput() {
                                // Your logic here, using only 'this' (the buffer)

                            }
                        }

//                    TextField(
//                        value = statusData.value,
//                        onValueChange =
//                            {
//                                viewModel.onStatusChange(it)
//                                val lastWord = it.trim().split(" ").last()
//                                if (lastWord.startsWith("@")) {
//                                    val searchString = lastWord.substringAfter("@")
//                                    if (searchString.isNotBlank()){
//                                        Log.d("StatusScreen","searchstring not blank $searchString")
//                                        viewModel.filterUsers(searchString)
//                                    }
//                                } else{
//                                    if (filteredUsersData.value.isNotEmpty())
//                                    viewModel.clearFilteredUserData()
//                                }
//                            },
//                        label = { Text("Enter your status") },
//                        minLines = 5,
//                        placeholder = { Text("Type @ to mention someone") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .focusRequester(focusRequester)
//                    )


//                            decorationBox = { innerTextField ->
//                                val text = statusData.value.text
//                                val annotatedString = buildAnnotatedString {
//                                    var currentIndex = 0
//                                    val regex = Regex("#(.*?)#") // to identify emailid ###([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)")
//                                    regex.findAll(text).forEach { result ->
//                                        ("regex range - ${result.range}")
//                                        val range = result.range
//                                        // Add text before the match
//                                        append(text.substring(currentIndex, range.first))
//                                        println("text substring - ${text.substring(currentIndex, range.first)}")
//                                        // Add the matched text in blue
//                                        withStyle(SpanStyle(
//                                            color = Color.Blue,
//                                            fontWeight = FontWeight.Bold
//                                        )) {
//                                            append(" ${result.value} ")
////                                            println(result.value.drop(3))
////                                            append(allUsersData.value.firstOrNull {
////                                                it.email == result.value.drop(3)
////                                            }?.username ?: "null")
//                                        }
//                                        currentIndex = range.last + 1
//                                    }
//                                    // Add remaining text
//                                    if (currentIndex < text.length) {
//                                        append(text.substring(currentIndex))
//                                    }
//                                }
//                                println("annoted string - $annotatedString")
////                                Box {
//                                    Text(
//                                        text = annotatedString,
//                                        style = TextStyle(fontSize = 20.sp)
//                                    )
//                                    innerTextField()
////                                }
//                            }


//                                println(value.text)
//                                val lastLine = value.text.lines().lastOrNull() ?: ""
//                                println("Last line $lastLine")
//                                val lastWord = lastLine.trim().split(" ").last()
//                                println("Last word $lastWord")
//                                if (lastWord.startsWith("@")) {
//                                    val searchString = lastWord.substringAfter("@")
//                                    if (searchString.isNotBlank()){
//                                        Log.d("StatusScreen","searchstring not blank $searchString")
//                                        viewModel.filterUsers(searchString)
//                                    }
//                                } else{
//                                    if (filteredUsersData.value.isNotEmpty())
//                                        viewModel.clearFilteredUserData()
//                                }
 */