package com.oleksandrstefanyshyn.servicedemoapp

object Common {
    private const val BASE_URL = "https://en.wikipedia.org/"
    val downloadService: DownloadService
        get() = RetrofitClient.getClient(BASE_URL).create(DownloadService::class.java)
}
