package com.oleksandrstefanyshyn.servicedemoapp

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadService {

    @GET
    @Streaming
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
}
