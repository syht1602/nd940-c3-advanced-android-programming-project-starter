package com.udacity

enum class DownloadType(val status: DownloadStatus) {
    GLIDE(DownloadStatus.SUCCESS),
    LOAD_APP(DownloadStatus.FAIL),
    RETROFIT(DownloadStatus.SUCCESS),
    NONE(DownloadStatus.FAIL)
}