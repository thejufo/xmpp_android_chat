package com.thejufo.chat.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thejufo.chat.data.network.XMPPConnection
import com.thejufo.chat.data.network.XMPPMessaging
import com.thejufo.chat.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Provides
  @Singleton
  fun providesXMPPConnection(): XMPPConnection {
    return XMPPConnection()
  }

  @Provides
  @Singleton
  fun providesXMPPMessaging(xmppConnection: XMPPConnection): XMPPMessaging {
    return XMPPMessaging(xmppConnection)
  }

  @Provides
  @Singleton
  fun providesAuthRepo(
    dataStore: DataStore<Preferences>,
    xmppConnection: XMPPConnection,
  ): AuthRepository {
    return AuthRepository(dataStore, xmppConnection)
  }
}