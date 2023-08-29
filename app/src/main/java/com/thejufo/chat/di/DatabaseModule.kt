package com.thejufo.chat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thejufo.chat.data.local.db.AppDatabase
import com.thejufo.chat.data.local.ChatsDao
import com.thejufo.chat.data.local.ContactsDao
import com.thejufo.chat.data.local.MessagesDao
import com.thejufo.chat.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

  // Provides a singleton instance of DataStore
  // Used to store user auth state and preferences
  @Provides
  fun provideDatastore(@ApplicationContext context: Context): DataStore<Preferences> {
    return context.dataStore
  }

  // Provides a singleton instance of AppDatabase(Room Database)
  // Used to store user contacts, conversations and messages
  @Provides
  @Singleton
  fun providesDatabase(@ApplicationContext context: Context): AppDatabase {
    return AppDatabase.getInstance(context)
  }

  @Provides
  @Singleton
  fun providesContactsDao(database: AppDatabase): ContactsDao {
    return database.contactsDao()
  }

  @Provides
  @Singleton
  fun providesChatsDao(database: AppDatabase): ChatsDao {
    return database.chatsDao()
  }

  @Provides
  @Singleton
  fun providesMessagesDao(database: AppDatabase): MessagesDao {
    return database.messagesDao()
  }
}