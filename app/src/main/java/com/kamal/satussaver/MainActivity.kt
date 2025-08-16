package com.kamal.satussaver

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.UriPermission
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kamal.satussaver.ui.theme.SatusSaverTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.kamal.satussaver.screen.GalleryStatus
import com.kamal.satussaver.screen.PreviewScreen
import java.io.File
import kotlin.getValue
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    private val viewModel: BaseViewModel by viewModels()

    @SuppressLint("ViewModelConstructorInComposable", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SatusSaverTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "dashboard") {
                    composable("dashboard") {
                        Dashboard(viewModel, navController = navController)
                    }
                    composable("MediaScreen") {
                        GalleryStatus(viewModel, navController)
                    }
                    composable("PreviewScreen/{index}") { backStackEntry ->
                        val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0

                        Log.d("mediaList", "PreviewScreenNavi: ${viewModel.mediaList.size}")
                        PreviewScreen(viewModel, index, navController)
                    }
                }
//                Dashboard(viewModel = BaseViewModel(),)

            }
        }
    }
}

@Composable
fun StatusCard(viewModel: BaseViewModel, navController: NavHostController) {
    Card(
        modifier = Modifier
            .clickable {
                viewModel.cardClicked.value = true
                Log.d("cardclicked", "StatusCard:11111 ${viewModel.cardClicked}")
            }
            .fillMaxWidth()
            .padding(top = 30.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {

        Box {
            Image(
                painter = painterResource(id = R.drawable.simplewa_bg),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp, 12.dp, 12.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Status Saver",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                )
                Text(
                    text = "Download & Save",
                    modifier = Modifier.padding(top = 5.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    )
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 10.dp, 10.dp, 0.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.swa_ic),
                        contentDescription = "Background",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }


        }

        Log.d("cardclicked", "StatusCard111111:${viewModel.cardClicked} ")
        if (viewModel.cardClicked.value) {
            loadStatus(viewModel, navController)
//            cardClicked=false

        }

    }
}

@Composable
fun loadStatus(viewModel: BaseViewModel, navController: NavHostController) {
    if (checkPermissionForDocumentTree(
            context = LocalContext.current,
            uriTree = Uri.parse(Constant.normalWhatappPath)
        )
    ) {
        val list = getStatusFiles(LocalContext.current, Uri.parse(Constant.normalWhatappPath))
        Log.d("cardclicked", "loadStatus: permission granded $list")
        viewModel.mediaList.clear()
        viewModel.mediaList.addAll(list)

        viewModel.cardClicked.value = false
        navController.navigate("MediaScreen")

    } else {
        Log.d("cardclicked", "loadStatus: permission not granded ")

        PermissionDialog(viewModel)


    }
}


@Composable
fun PermissionDialog(viewModel: BaseViewModel) {
    val context = LocalContext.current

    // SAF launcher to pick .Statuses folder
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                Log.d("PermissionDialog", "Selected URI: $uri")

                // Persist the granted URI permission
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val list = getStatusFiles(context, Uri.parse(Constant.normalWhatappPath))
                viewModel.mediaList.clear()
                viewModel.mediaList.addAll(list)

                // You can store this URI in shared preferences or data store for reuse
            }
        } else {
            Log.d("PermissionDialog", "User cancelled folder picker")
        }

        // Always close the dialog
        viewModel.cardClicked.value = false
    }

    if (!viewModel.cardClicked.value) return

    Dialog(onDismissRequest = {
        viewModel.cardClicked.value = false
    }) {
        AnimatedVisibility(
            viewModel.cardClicked.value,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.8f)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Permission Required",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This feature needs access to storage. Please allow permission to continue.",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                viewModel.cardClicked.value = false
                                Log.d("PermissionDialog", "User Cancelled")
                            }
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (isPackageInstalled(context, Constant.whatsAppPackage)) {
                                    val intent = createStatusFolderIntent(context)
                                    if (intent != null) {
                                        launcher.launch(intent as Intent)
                                    } else {
                                        Log.e("PermissionDialog", "Intent creation failed")
                                        viewModel.cardClicked.value = false
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Whats app is not install",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        ) {
                            Text("Grant")
                        }
                    }
                }
            }
        }
    }
}

fun createStatusFolderIntent(context: Context): Intent {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)

    val statusFolderUri =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AWhatsApp%2FMedia%2F.Statuses")

    intent.putExtra("android.provider.extra.INITIAL_URI", statusFolderUri)
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

    return intent
}


fun isPackageInstalled(context: Context, pakageName: String?): Boolean {
    return try {
        val packageManager = context?.packageManager
        println("ImageFragmentpakage.isPackageInstalled   $packageManager")
        Log.d("pakage", "isPackageInstalled: $pakageName")
        packageManager?.getPackageInfo(pakageName!!, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        println("StatusFragment.isPackageInstalled " + e.message)
        false
    }
}


@RequiresApi(Build.VERSION_CODES.N)
private fun checkPermissionForDocumentTree(context: Context, uriTree: Uri): Boolean {
    return context.contentResolver.persistedUriPermissions.stream()
        .anyMatch { permission: UriPermission -> permission.uri == uriTree }
}

@Composable
fun LazyRowWithRoundedImages(viewModel: BaseViewModel) {
    val context = LocalContext.current
    viewModel.loadStatusFiles(
        context = context,
        treeUri = Constant.normalWhatappPath.toUri()
    )
    if (viewModel.mediaList.isNotEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.mediaList) { uri ->
                if (uri.toString().endsWith(".jpg")) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }

            }
        }
    } else {
        DrawableImage(R.drawable.no_recent_icon)
    }
}

@Composable
fun DrawableImage(icon: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "My Image",
            modifier = Modifier.padding(top = 10.dp, bottom = 5.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Dashboard(viewModel: BaseViewModel, navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 34.dp, start = 16.dp, end = 16.dp)
    ) {

        StatusCard(viewModel, navController)
        HeadLines("Recently Uploaded Status")
        LazyRowWithRoundedImages(viewModel)
        HeadLines("Downloaded Status")
        val downloadList = getDownloadedStatuses(LocalContext.current)
        Log.d("downloadList", "Dashboard: downloadList  $downloadList")
        DownloadStatusCarousel(downloadList)
        HeadLines("Birthday Frames")
        val framesList = arrayListOf(
            R.drawable.b1,
            R.drawable.b1,
            R.drawable.b1,
            R.drawable.b1
        )



    }
}

@Composable
fun HeadLines(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ), modifier = Modifier.padding(top = 10.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadStatusCarousel(downloadList: List<Uri>) {
if (downloadList.isNotEmpty()){
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { downloadList.count() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp, bottom = 16.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val uri = downloadList[i]
        Image(
            painter = rememberAsyncImagePainter(model = uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(205.dp)
                .width(205.dp)
                .maskClip(MaterialTheme.shapes.extraLarge)
        )
    }
}else{
    DrawableImage(R.drawable.no_download_ic)
}}





fun getStatusFiles(context: Context, treeUri: Uri): List<Uri> {
    val documentFile = DocumentFile.fromTreeUri(context, treeUri)
    if (documentFile != null && documentFile.isDirectory) {
        return documentFile.listFiles()
            .filter { it.isFile && (it.name?.endsWith(".jpg") == true || it.name?.endsWith(".mp4") == true) }
            .mapNotNull { it.uri }
    }
    return emptyList()
}

fun getDownloadedStatuses(context: Context): List<Uri> {
    val downloadDir = File(context.getExternalFilesDir(null), "StatusSaver")

    if (!downloadDir.exists() || !downloadDir.isDirectory) {
        return emptyList()
    }

    return downloadDir.listFiles { file ->
        file.extension.lowercase() in listOf("jpg", "jpeg", "png", "mp4")
    }?.map { file ->
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // You must declare FileProvider in manifest
            file
        )
    } ?: emptyList()
}

@Composable
fun ImageWithFramePicker() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        selectedImageUri?.let { uri ->
            ImageWithFrame(uri = uri)
        }
    }
}

@Composable
fun ImageWithFrame(uri: Uri) {
    val context = LocalContext.current

    // Example: pick frame from framesList[0]
    val frameRes = R.drawable.b1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // User selected image
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        // Frame overlay
        Image(
            painter = painterResource(id = frameRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}


