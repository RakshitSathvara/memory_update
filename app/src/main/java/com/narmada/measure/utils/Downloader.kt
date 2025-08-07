package com.narmada.measure.utils

interface Downloader {
    fun downloadFile(url: String): Long
}