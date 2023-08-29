package com.thejufo.chat.data.local.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.thejufo.chat.data.local.ChatsDao
import com.thejufo.chat.data.local.ContactsDao
import com.thejufo.chat.data.local.MessagesDao
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.Contact
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.workers.PopulateContactsWorker

@Database(
  entities = [Contact::class, Chat::class, Message::class],
  version = 51,
  exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun contactsDao(): ContactsDao
  abstract fun chatsDao(): ChatsDao
  abstract fun messagesDao(): MessagesDao

  companion object {
    private const val DATABASE_NAME = "chats"

    @Volatile
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return instance ?: synchronized(this) {
        instance ?: buildDatabase(context).also { instance = it }
      }
    }

    private fun buildDatabase(context: Context): AppDatabase {
      return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        .addCallback(object : Callback() {
          override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("AppDatabase", "populating with data..")
            val request = OneTimeWorkRequestBuilder<PopulateContactsWorker>().build()
            WorkManager.getInstance(context).enqueue(request)
          }
        })
        .fallbackToDestructiveMigration()
        .build()
    }
  }
}