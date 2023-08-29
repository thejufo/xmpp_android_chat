package com.thejufo.chat.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.thejufo.chat.data.network.XMPPConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepository @Inject constructor(
  private val dataStore: DataStore<Preferences>,
  private val xmppConnection: XMPPConnection,
) {

  suspend fun getJid(): Flow<String> {
    return dataStore.data.catch { emit(emptyPreferences()) }.map {
      it[stringPreferencesKey("username")] ?: ""
    }
  }

  suspend fun login(credentials: Pair<String, String>) {
    Log.d("AuthRepository", "Logging in")
    xmppConnection.login(credentials)
    dataStore.edit { preferences ->
      val username = stringPreferencesKey("username")
      val password = stringPreferencesKey("password")
      preferences[username] = credentials.first
      preferences[password] = credentials.second
    }
  }

  suspend fun isLoggedIn(): Boolean {
    try {
      if (xmppConnection.isLoggedIn()) {
        return true
      }
      val credentials =
        dataStore.data.catch { emptyPreferences() }.map(::getCredentials).firstOrNull()
      if (credentials != null) {
        xmppConnection.login(credentials)
        return true
      }
      return false
    } catch (e: Exception) {
      return false
    }
  }

  suspend fun logout() {
    dataStore.edit { preferences ->
      preferences.remove(stringPreferencesKey("username"))
      preferences.remove(stringPreferencesKey("password"))
    }
    xmppConnection.disconnect()
  }


  private fun getCredentials(preferences: Preferences): Pair<String, String>? {
    val username = preferences[stringPreferencesKey("username")]
    val password = preferences[stringPreferencesKey("password")]
    return if (username == null || password == null) {
      null
    } else {
      Pair(username, password)
    }
  }
}