package com.thejufo.chat.ui.chats

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thejufo.chat.R
import com.thejufo.chat.data.models.ChatWithMessages
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.data.repository.AuthRepository
import com.thejufo.chat.data.repository.ChatsRepository
import com.thejufo.chat.data.repository.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatsViewModel @Inject constructor(
  private val messagesRepository: MessagesRepository,
  private val chatsRepository: ChatsRepository,
  private val authRepository: AuthRepository,
  private val application: Application,
) : AndroidViewModel(application) {

  val chats = MutableStateFlow<List<ChatWithMessages>>(emptyList())
  val navigateToLogin = MutableStateFlow(false)

  init {

    viewModelScope.launch {
      chatsRepository.chats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
        .collect {
          chats.value = it
        }
    }

    viewModelScope.launch {
      chatsRepository.incomingMessages().collect {
        val chatId = getChatId(it.first, false)
        val message = Message(
          chatId = chatId,
          senderId = it.first,
          text = it.second,
          status = Message.Status.RECEIVED,
          timestamp = System.currentTimeMillis()
        )
        val messageId = messagesRepository.receiveMessage(message)

        // check notification permission
        showNotification(message.copy(id = messageId))
      }
    }

    viewModelScope.launch {
      chatsRepository.mucInvitations().collect { it: Pair<String, String> ->
        val chatId = getChatId(it.first, true)
        val message = Message(
          chatId = chatId,
          senderId = it.second,
          text = "${it.second} added you to this group",
          status = Message.Status.RECEIVED,
          timestamp = System.currentTimeMillis()
        )
        messagesRepository.receiveMessage(message)
      }
    }
  }

  fun listenGroupMessages(groups: List<String>) {
    viewModelScope.launch {
      chatsRepository.groupIncomingMessages(groups.map { it }).collect {
        val chatId = getChatId(it.first, false)
        val message = Message(
          chatId = chatId,
          senderId = it.first,
          text = it.second,
          status = Message.Status.RECEIVED,
          timestamp = System.currentTimeMillis()
        )
        val messageId = messagesRepository.receiveMessage(message)

        // check notification permission
        showNotification(message.copy(id = messageId))
      }
    }
  }

  private suspend fun getChatId(remoteId: String, isGroupChat: Boolean): Long {
    var chatId = chatsRepository.getChatByRemoteId(remoteId)?.id
    if (chatId == null) {
      chatId = chatsRepository.createChat(remoteId, isGroupChat)
    }
    return chatId
  }


  private fun showNotification(message: Message) {
    // if android version is greater than or equal to oreo and notification permission is not granted
    // then do nothing
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ActivityCompat.checkSelfPermission(
          application.applicationContext,
          Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    val manager = NotificationManagerCompat.from(application.applicationContext)
    val notification = NotificationCompat.Builder(application.applicationContext, "messages")
      .setContentTitle(message.senderId)
      .setContentText(message.text)
      .setSmallIcon(R.drawable.ic_account)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_MESSAGE)
      .build()
    manager.notify(message.id!!.toInt(), notification)
  }

  fun logout() {
    viewModelScope.launch {
      authRepository.logout()
      chatsRepository.deleteAll()
      messagesRepository.deleteAll()
      navigateToLogin.value = true
    }
  }
}