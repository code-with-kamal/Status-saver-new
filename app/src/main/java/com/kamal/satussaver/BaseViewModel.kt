package com.kamal.satussaver

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel

class BaseViewModel : ViewModel() {
    val cardClicked = mutableStateOf(false)
    val mediaType = mutableStateOf(0)

    var mediaList = mutableStateListOf<Uri>()
    var imageList = mutableStateListOf<Uri>()
    var videoList = mutableStateListOf<Uri>()



    fun loadStatusFiles(context: Context, treeUri: Uri) {
        val files = getStatusFiles(context, treeUri)
        mediaList.clear()
        imageList.clear()
        videoList.clear()
        mediaList.addAll(files)

        imageList.addAll(files.filter { it.toString().endsWith(".jpg", ignoreCase = true) })
        videoList.addAll(files.filter { it.toString().endsWith(".mp4", ignoreCase = true) })

        Log.d("files", "loadStatusFiles: $files")

    }

    private fun getStatusFiles(context: Context, treeUri: Uri): List<Uri> {
        val documentFile = DocumentFile.fromTreeUri(context, treeUri)
        if (documentFile != null && documentFile.isDirectory) {
            return documentFile.listFiles()
                .filter { it.isFile && (it.name?.endsWith(".jpg") == true || it.name?.endsWith(".mp4") == true) }
                .mapNotNull { it.uri }
        }
        return emptyList()
    }

}