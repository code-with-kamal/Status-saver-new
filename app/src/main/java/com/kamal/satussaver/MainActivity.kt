package com.kamal.satussaver

import android.content.Context
import android.content.UriPermission
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kamal.satussaver.ui.theme.SatusSaverTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SatusSaverTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    Dashboard()
                }
            }
        }
    }
}

@Composable
fun StatusCard() {
    var cardClicked by remember { mutableStateOf(false) }


    Card(

        modifier = Modifier
            .clickable {
                cardClicked = true
                Log.d("cardclicked", "StatusCard:11111 $cardClicked")
            }
            .fillMaxWidth()
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

            Log.d("cardclicked", "StatusCard111111:$cardClicked ")
        if (cardClicked) {
            loadStatus()
//            cardClicked=false

        }

    }
}

@Composable
fun loadStatus() {
    if (checkPermissionForDocumentTree(
            context = LocalContext.current,
            uriTree = Uri.parse(Constant.normalWhatappPath)
        )
    ){
        Log.d("cardclicked", "loadStatus: permission granded ")
    }else{
        Log.d("cardclicked", "loadStatus: permission not granded ")

          PermissionDialog()




    }
}


@Composable
fun PermissionDialog() {
    var visible by remember { mutableStateOf(true) }

    if (!visible) return

    Dialog(onDismissRequest = { visible = false }) {
        AnimatedVisibility(
            visible = visible,
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
                                visible = false
                                Log.d("PermissionDialog", "User Cancelled")
                                // Optional: Cancel action logic
                            }
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                visible = false
                                Log.d("PermissionDialog", "User Granted")
                                // Optional: Launch permission intent here
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



@RequiresApi(Build.VERSION_CODES.N)
private fun checkPermissionForDocumentTree(context: Context, uriTree: Uri): Boolean {
    return context.contentResolver.persistedUriPermissions.stream()
        .anyMatch { permission: UriPermission -> permission.uri == uriTree }
}

@Composable
fun LazyRowWithRoundedImages() {
    val imageList = listOf(
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.swa_ic,
        R.drawable.simplewa_bg
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(imageList) { imageRes ->
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Rounded Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
        }
    }
}


@Composable
fun Dashboard() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 34.dp, start = 16.dp, end = 16.dp)
    ) {

        StatusCard()
        HeadLines("Recently Uploaded Status")
        LazyRowWithRoundedImages()
        HeadLines("Downloaded Status")

//        val imageList = listOf(
//            R.drawable.ic_launcher_background,
//            R.drawable.ic_launcher_background,
//            R.drawable.ic_launcher_background,
//            R.drawable.swa_ic,
//            R.drawable.simplewa_bg
//        )
//        HorizontalImageCarousel(imageList)
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





@Composable
fun AnimatedCarousel(
    imageList: List<Int>,
    modifier: Modifier = Modifier,
    imageHeight: Dp = 200.dp,
    cornerRadius: Dp = 16.dp
) {
    val pagerState = rememberPagerState { imageList.size }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 48.dp), // show adjacent pages
        pageSpacing = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(imageHeight)
    ) { page ->
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val scale = 1f - (0.25f * pageOffset.absoluteValue.coerceIn(0f, 1f))

        Image(
            painter = painterResource(id = imageList[page]),
            contentDescription = "Image $page",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .scale(scale)
                .clip(RoundedCornerShape(cornerRadius))
        )
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SatusSaverTheme {
        Dashboard()
    }
}