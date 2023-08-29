package com.thejufo.chat.data.network

import android.util.Log
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.ping.PingManager
import javax.inject.Inject

/**
 * Responsible for connecting to the XMPP server
 * and making API calls
 **/
class XMPPConnection @Inject constructor() : ConnectionListener {

  var connection: XMPPTCPConnection? = null
    private set

  private fun connect(credentials: Pair<String, String>) {
    val config = XMPPTCPConnectionConfiguration
      .builder()
      .setXmppDomain("uatchat2.waafi.com")
      .setUsernameAndPassword(credentials.first, credentials.second)
      .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
      .build()

    connection = XMPPTCPConnection(config).apply {
      addConnectionListener(this@XMPPConnection)
      if (!isConnected) {
        connect()
      }
      if (!isAuthenticated) {
        login()
      }

      PingManager.setDefaultPingInterval(60 * 1)
    }
  }

  override fun connected(connection: XMPPConnection?) {
    super.connected(connection)
    Log.d("XMPPConnection", "connected")
  }

  override fun connectionClosed() {
    super.connectionClosed()
    Log.d("XMPPConnection", "connectionClosed")
  }

  override fun connectionClosedOnError(e: Exception?) {
    super.connectionClosedOnError(e)
    Log.d("XMPPConnection", "connectionClosedOnError: ${e?.message}")
  }

  fun login(credentials: Pair<String, String>) {
    connect(credentials)
  }

  fun isLoggedIn(): Boolean {
    return connection?.isAuthenticated ?: false
  }

  fun disconnect() {
    connection?.disconnect()
    connection = null
  }
}