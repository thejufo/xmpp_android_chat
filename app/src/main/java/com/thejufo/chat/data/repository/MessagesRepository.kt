package com.thejufo.chat.data.repository

import android.util.Log
import com.thejufo.chat.data.local.MessagesDao
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.data.network.XMPPMessaging
import javax.inject.Inject

class MessagesRepository @Inject constructor(
  private val messagesDao: MessagesDao,
  private val xmppMessaging: XMPPMessaging
) {

  fun getMessages(chatId: Long) = messagesDao.getMessages(chatId)

  suspend fun sendMessage(chat: Chat, message: Message) {
    val id = messagesDao.insertMessage(message)
    try {
      Log.d("MessagesRepository", "Sending message to ${chat.isGroupChat}")
      if (chat.isGroupChat) {
        xmppMessaging.sendGroupMessage(chat.chatName, message.text)
      } else {
        xmppMessaging.sendMessage(chat.chatName, message.text)
      }
      messagesDao.updateMessage(message.copy(id = id, status = Message.Status.SENT))
    } catch (e: Exception) {
      e.printStackTrace()
      messagesDao.updateMessage(message.copy(id = id, status = Message.Status.Failed))
    }
  }

  suspend fun receiveMessage(message: Message) = messagesDao.insertMessage(message)

  suspend fun deleteAll() = messagesDao.deleteAll()
}