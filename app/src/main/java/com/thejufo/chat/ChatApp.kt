package com.thejufo.chat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApp: Application() {

  override fun onCreate() {
    super.onCreate()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel("messages", "Messages", IMPORTANCE_HIGH)
      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(channel)
    }
  }
}