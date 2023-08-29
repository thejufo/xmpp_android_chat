package com.thejufo.chat.data.repository

import com.thejufo.chat.data.local.ChatsDao
import com.thejufo.chat.data.local.MessagesDao
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.ChatWithMessages
import com.thejufo.chat.data.models.Contact
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.data.network.XMPPMessaging
import kotlinx.coroutines.flow.Flow
import org.jivesoftware.smackx.muc.MultiUserChat
import javax.inject.Inject

class ChatsRepository @Inject constructor(
  private val chatsDao: ChatsDao,
  private val xmppMessaging: XMPPMessaging
) {

  val chats = chatsDao.getChatsWithLastMessage()

  suspend fun createChat(chatName: String, isGroupChat: Boolean): Long {
    return with(Chat(chatName = chatName, isGroupChat = isGroupChat)) {
      chatsDao.insertChat(this)
    }
  }

  suspend fun getChatByRemoteId(jid: String): Chat? {
    return chatsDao.getChatByRemoteId(jid)
  }

  suspend fun deleteChat(chat: Chat) = chatsDao.deleteChat(chat)

  suspend fun deleteAll() = chatsDao.deleteAll()

  fun incomingMessages() = xmppMessaging.inComingMessages()

  fun groupIncomingMessages(groups: List<String>) = xmppMessaging.groupIncomingMessages(groups)

  fun createGroup(participants: List<Contact>, name: String) {
    return xmppMessaging.createGroup(participants, name)
  }

  fun mucInvitations() = xmppMessaging.mucInvitations()
}