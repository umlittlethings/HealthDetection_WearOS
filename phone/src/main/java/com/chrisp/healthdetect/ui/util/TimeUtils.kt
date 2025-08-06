package com.chrisp.healthdetect.ui.util

import java.sql.Timestamp
import java.util.concurrent.TimeUnit

fun formatTimeAgo (timestamp: Long): String {
    val now = System.currentTimeMillis()
    val seconds = TimeUnit.MILLISECONDS.toSeconds(now - timestamp)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(now - timestamp)
    val hours = TimeUnit.MILLISECONDS.toHours(now - timestamp)

    return when {
        seconds < 60 -> "Baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        else -> "Lebih dari sehari lalu"
    }
}