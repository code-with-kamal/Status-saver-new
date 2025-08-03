package com.kamal.satussaver.screen

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kamal.satussaver.BaseViewModel
import com.kamal.satussaver.Constant
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

//@Composable
//fun GalleryStatus(viewModel: BaseViewModel) {
//val context=LocalContext.current
//    LaunchedEffect(Unit) {
//        viewModel.loadStatusFiles(
//            context = context,
//            treeUri = Uri.parse(Constant.normalWhatappPath)
//        )
//        Log.d("list1", "333333333333: ${viewModel.mediaList.size}")
//
//    }
//
//
//    TabRowWithPager(viewModel,context)
//}



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GalleryStatus(viewModel: BaseViewModel, navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadStatusFiles(
            context = context,
            treeUri = Uri.parse(Constant.normalWhatappPath)
        )
        Log.d("list1", "Media List Size: ${viewModel.mediaList.size}")
    }

    Scaffold(
        modifier = Modifier.padding(top = 30.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statuses",
                        color = Color.White,
                        fontSize = 22.sp
                    )
                },
                backgroundColor = Color(0xFF8D6ABE),
                navigationIcon = {
                    IconButton(onClick = {

                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = true }

                        } }
                    ){
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) {
        TabRowWithPager(viewModel, context, navController)
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabRowWithPager(viewModel: BaseViewModel, context: Context, navController: NavController) {
    val tabs = listOf("Images", "Videos")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()



    val images by remember {
        derivedStateOf {
            viewModel.mediaList.filter {
                it.toString().endsWith(".jpg")
            }
        }
    }
    val videos by remember {
        derivedStateOf {
            viewModel.mediaList.filter {
                it.toString().endsWith(".mp4")
            }
        }
    }
    Log.d("vides", "TabRowWithPager: $videos")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp)
    ) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> {
                    viewModel.mediaType.value=0
                    StatusGrid(viewModel.imageList, context, navController)}
                1 -> {
                    viewModel.mediaType.value=1
                    StatusGrid(viewModel.videoList, context, navController)
                }
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun StatusGrid(
    media: List<Uri>,
    context: Context,
    navController: NavController
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(media) { index, uri ->
            val isDownloaded = remember(uri) {
                derivedStateOf { isStatusDownloaded(context, uri) }
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        navController.navigate("PreviewScreen/$index")
                    }
            ) {
                // Media Preview (Image or Video)
                if (uri.toString().endsWith(".jpg")) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val thumbnail = remember(uri) {
                        getVideoThumbnail(context, uri)?.asImageBitmap()
                    }

                    if (thumbnail != null) {
                        Image(
                            bitmap = thumbnail,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Video", color = Color.White)
                        }
                    }
                }

                // ✅ Download or Check Icon
                if (!isDownloaded.value) {
                    IconButton(
                        onClick = {
                            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "status.jpg"
                            val success = downloadStatus(context, uri, fileName)
                            if (success) {
                                Toast.makeText(context, "Downloaded!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to download", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                } else {
                    // ✅ Show tick icon only
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Downloaded",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape),
                        tint = Color.Green
                    )
                }
            }
        }
    }
}




//@Composable
//fun StatusGrid(
//    media: List<Uri>,
//    context: Context,
//    navController: NavController
//) {
//    Log.d("mediaList", "PreviewScreenClick: $media")
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier.padding(8.dp),
//
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        itemsIndexed(media) { itemsIndexed,uri ->
//            if (uri.toString().endsWith(".jpg")) {
//                AsyncImage(
//                    model = uri,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .clickable {
//                            navController.navigate("PreviewScreen/$itemsIndexed")
//
//
//                        }
//                        .aspectRatio(1f)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
//
//
//                val thumbnail = remember(uri) {
//                    getVideoThumbnail(context, uri)?.asImageBitmap()
//                }
//
//                if (thumbnail != null) {
//                    Image(
//                        bitmap = thumbnail,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .clickable {
//                                navController.navigate("PreviewScreen/$itemsIndexed")
//
//                                Log.d("mediaList", "PreviewScreenClickVideo: $itemsIndexed")
//                            }
//                            .aspectRatio(1f)
//                            .clip(RoundedCornerShape(8.dp)),
//                        contentScale = ContentScale.Crop
//                    )
//                    val isDownloaded = isStatusDownloaded(context, uri)
//                    if (isDownloaded){
//
//                    }
//                } else {
//                    // Fallback if thumbnail is null
//                    Box(
//                        modifier = Modifier
//                            .aspectRatio(1f)
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(Color.Gray),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text("Video", color = Color.White)
//                    }
//                }
//
//            }
//        }
//    }
//}


fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val bitmap = retriever.frameAtTime
        retriever.release()
        bitmap
    } catch (e: Exception) {
        null
    }
}

fun downloadStatus(context: Context, uri: Uri, fileName: String): Boolean {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return false

        val downloadsDir = File(context.getExternalFilesDir(null), "StatusSaver")
        if (!downloadsDir.exists()) downloadsDir.mkdirs()

        val outFile = File(downloadsDir, fileName)
        val outputStream = FileOutputStream(outFile)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()

        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}


fun isStatusDownloaded(context: Context, uri: Uri): Boolean {
    val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: return false
    val downloadDir = File(context.getExternalFilesDir(null), "StatusSaver")
    val targetFile = File(downloadDir, fileName)
    return targetFile.exists()
}




//@Composable
//fun GalleryStatus(viewModel: BaseViewModel) {
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Gallery Status",
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(bottom = 12.dp)
//        )
//
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(viewModel.mediaList) { uri ->
//                if (uri.toString().endsWith(".jpg")) {
//                    AsyncImage(
//                        model = uri,
//                        contentDescription = "Status Image",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp)
//                            .clip(RoundedCornerShape(12.dp))
//                    )
//                } else if (uri.toString().endsWith(".mp4")) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp)
//                            .background(Color.Gray),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "Video: ${uri.lastPathSegment}",
//                            color = Color.White
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
