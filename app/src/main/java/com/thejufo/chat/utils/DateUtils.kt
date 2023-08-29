package com.thejufo.chat.utils

import android.text.format.DateUtils

fun formatDate(timestamp: Long): String {
  return try {
    if (DateUtils.isToday(timestamp)) {
      "Today"
    } else if (DateUtils.isToday(timestamp - DateUtils.DAY_IN_MILLIS)) {
      "Yesterday"
    } else {
      DateUtils.getRelativeTimeSpanString(timestamp).toString()
    }
  } catch (e: Exception) {
    e.printStackTrace()
    "-"
  }
}
