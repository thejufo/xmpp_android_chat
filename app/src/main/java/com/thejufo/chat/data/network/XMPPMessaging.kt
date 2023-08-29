package com.thejufo.chat.data.network

import android.util.Log
import com.thejufo.chat.data.models.Contact
import com.thejufo.chat.data.models.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.jivesoftware.smack.MessageListener
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.chat2.IncomingChatMessageListener
import org.jivesoftware.smackx.muc.InvitationListener
import org.jivesoftware.smackx.muc.MucConfigFormManager
import org.jivesoftware.smackx.muc.MucEnterConfiguration
import org.jivesoftware.smackx.muc.MultiUserChat
import org.jivesoftware.smackx.muc.MultiUserChatManager
import org.jivesoftware.smackx.muclight.MultiUserChatLightManager
import org.jivesoftware.smackx.xdata.form.FillableForm
import org.jivesoftware.smackx.xdata.form.Form
import org.jivesoftware.smackx.xdata.packet.DataForm
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.FullJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.jid.parts.Resourcepart
import javax.inject.Inject

class XMPPMessaging @Inject constructor(
  private val xmppConnection: XMPPConnection
) {

  private val chatManager = ChatManager.getInstanceFor(xmppConnection.connection)
  private val mucManager = MultiUserChatManager.getInstanceFor(xmppConnection.connection)

  fun sendMessage(chatName: String, text: String) {
    val jid = JidCreate.entityBareFrom("$chatName@uatchat2.waafi.com")

    chatManager.chatWith(jid).send(text)
  }

  fun sendGroupMessage(chatName: String, text: String) {
    val jid = JidCreate.entityBareFrom("$chatName@muclight.uatchat2.waafi.com")
    Log.d("XMPPMessaging", "Sending message to $jid")
    val muc = mucManager.getMultiUserChat(jid)
    muc.sendMessage(text)
  }

  fun inComingMessages() = callbackFlow {
    val listener = IncomingChatMessageListener { from, content, _ ->
      val jid = from.split("@")[0]
      trySend(Pair(jid, content.body))
    }
    chatManager.addIncomingListener(listener)

    awaitClose {
      chatManager.removeIncomingListener(listener)
      channel.close()
    }
  }

  fun groupIncomingMessages(groups: List<String>) = callbackFlow {
    val chats = mutableListOf<MultiUserChat>()
    val listener = MessageListener { content ->
      Log.d("NewMessage", "Message received ${content.body}")
      val jid = content.from.split("@")[0]
      if (jid == xmppConnection.connection?.user?.asEntityBareJidIfPossible()?.split("@")?.get(0)) {
        return@MessageListener
      }
      trySend(Pair(jid, content.body))
    }

    for (group in groups) {
      val jid = JidCreate.entityBareFrom("$group@muclight.uatchat2.waafi.com")
      Log.d("XMPPMessaging", "Listening to $jid")
      val muc = mucManager.getMultiUserChat(jid)
      muc.addMessageListener(listener)
      chats.add(muc)
    }

    awaitClose {
      for (chat in chats) {
        chat.removeMessageListener(listener)
      }
      channel.close()
    }
  }

  fun groupIncomingMessages() = callbackFlow {
    val chats = mutableListOf<MultiUserChat>()
    val listener = MessageListener { content ->
      val jid = content.from.split("@")[0]
      if (jid == xmppConnection.connection?.user?.asEntityBareJidIfPossible()?.split("@")?.get(0)) {
        return@MessageListener
      }
      trySend(Pair(jid, content.body))
    }

    val groups = mucManager.joinedRooms


    for (group in groups) {
      val jid = JidCreate.entityBareFrom("$group@muclight.uatchat2.waafi.com")
      val muc = mucManager.getMultiUserChat(jid)
      muc.addMessageListener(listener)
      chats.add(muc)
    }

    awaitClose {
      for (chat in chats) {
        chat.removeMessageListener(listener)
      }
      channel.close()
    }
  }

  fun createGroup(participants: List<Contact>, nickName: String) {

    val mucJid = JidCreate.entityBareFrom("$nickName@muclight.uatchat2.waafi.com")
    val muc = mucManager.getMultiUserChat(mucJid)
    muc.create(Resourcepart.from(nickName))

    val form = muc.configurationForm
    val submitForm = form.fillableForm
    submitForm.setAnswer("muc#roomconfig_publicroom", true)
    submitForm.setAnswer("muc#roomconfig_roomname", nickName)
    submitForm.setAnswer("muc#roomconfig_persistentroom", true)
    submitForm.setAnswer("muc#roomconfig_membersonly", false)
    submitForm.setAnswer("muc#roomconfig_allowinvites", true)
    submitForm.setAnswer("muc#roomconfig_maxusers", 100)
    submitForm.setAnswer("muc#roomconfig_whois", "anyone")
    submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", false)

    muc.sendConfigurationForm(submitForm)

    for (participant in participants) {
      val jid = JidCreate.entityBareFrom(participant.jid)
      muc.invite(jid, "Join us")
    }

   muc.invite(JidCreate.entityBareFrom(xmppConnection.connection?.user?.asEntityBareJidIfPossible()), "Join us")
  }

  fun mucInvitations() = callbackFlow<Pair<String, String>> {
    val listener =
      InvitationListener { _, muc, jid, _, password, _, _ ->
        try {
          xmppConnection.connection?.user?.asEntityBareJidIfPossible()?.let {
            val nickName = Resourcepart.from(it.split("@")[0])
            Log.d("XMPPMessaging", "$nickName")
            muc.join(nickName, password)
          }

          val jid2 = mucManager.getMultiUserChat(muc.room)
          Log.d("XMPPMessaging", "Invitation received from ${jid2.room}")
          trySend(Pair(muc.room.split("@")[0], jid.split("@")[0]))
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    mucManager.addInvitationListener(listener)

    awaitClose {
      mucManager.removeInvitationListener(listener)
      channel.close()
    }
  }
}