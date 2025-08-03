package com.kamal.satussaver.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.rememberPagerState
import com.kamal.satussaver.BaseViewModel
import com.kamal.satussaver.Constant
import androidx.compose.runtime.key
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PreviewScreen( viewModel: BaseViewModel,
                   startIndex: Int,
                   navController: NavHostController){
    Scaffold(
        modifier = Modifier.padding(top = 30.dp),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Preview",
                        color = Color.White,
                        fontSize = 22.sp
                    )
                },
                backgroundColor = Color(0xFF8D6ABE),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
        PreviewScreenContent(viewModel,startIndex,navController)
    }
}
@Composable
fun PreviewScreenContent(
    viewModel: BaseViewModel,
    startIndex: Int,
    navController: NavHostController
) {
    var currentIndex by remember { mutableStateOf(startIndex) }

    val isImage = viewModel.mediaType.value == 0
    val imageList = viewModel.imageList
    val videoList = viewModel.videoList

    Log.d("mediatype", "PreviewScreen:$startIndex $isImage")
    val mediaList = if (isImage) imageList else videoList
    if (mediaList.isEmpty()) {
        Text(
            "No media available",
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        return
    }

    val uri = mediaList.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

//        Row(
//            modifier = Modifier.padding(30.dp)
//                .fillMaxWidth()
//                .background(Color.DarkGray)
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                contentDescription = "Back",
//                tint = Color.White,
//                modifier = Modifier.clickable { navController.popBackStack() }
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Text("Preview", color = Color.White)
//        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            uri?.let {
                when {
                    isImage -> {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    !isImage -> {
                        VideoPlayer(uri = it)


                    }

                    else -> {
                        Text("Unsupported format", color = Color.White)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Text("Previous")
            }

            Button(
                onClick = { if (currentIndex < mediaList.size - 1) currentIndex++ },
                enabled = currentIndex < mediaList.size - 1
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current

    key(uri) {
        AndroidView(
            factory = {
                VideoView(context).apply {
                    setVideoURI(uri)
                    setMediaController(MediaController(context).apply {
                        setAnchorView(this@apply)
                    })
                    setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.isLooping = false
                        start()
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}


