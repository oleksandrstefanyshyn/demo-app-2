package com.oleksandrstefanyshyn.servicedemoapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File

class MainViewModel : ViewModel() {

    sealed class DownloadState {
        object None : DownloadState()
        object InProgress : DownloadState()
        object Success : DownloadState()
        class Error(val reasonString: String?, val reasonRes: Int) : DownloadState()

        val inProgress: Boolean
            get() = this is InProgress
    }

    private val downloadService = Common.downloadService
    val downloadState = MutableLiveData<DownloadState>(DownloadState.None)

    fun download(url: String, path: File?) {
        downloadState.value = DownloadState.InProgress
        viewModelScope.launch(IO) {
            val responseBody = downloadService.downloadFile(url).body()
            val result = saveFile(responseBody, path)
            withContext(Main) {
                downloadState.value = result
            }
        }
    }

    private fun saveFile(body: ResponseBody?, path: File?): DownloadState {
        if (body == null)
            return DownloadState.Error(null, R.string.no_response)
        try {
            body.byteStream().use { input ->
                path?.outputStream().use { output ->
                    output?.let { input.copyTo(it) }
                    return DownloadState.Success
                }
            }
        } catch (e: Exception) {
            Log.e("saveFile", e.toString())
            return DownloadState.Error(e.message, R.string.unknown_error)
        }
    }
}
