package com.thejufo.chat.ui.conversation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.data.repository.AuthRepository
import com.thejufo.chat.data.repository.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
  private val authRepository: AuthRepository,
  private val messagesRepository: MessagesRepository,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {

  val currentUserJid = MutableStateFlow("")
  val messages = MutableStateFlow<List<Message>>(emptyList())

  init {
    viewModelScope.launch {
      currentUserJid.value = authRepository.getJid().first()

      // retrieve messages
      val chat = savedStateHandle.get<Chat>("chat")
      if (chat?.id != null) {
        messagesRepository.getMessages(chat.id!!)
          .collect {
            messages.value = it
          }
      }
    }
  }

  fun getMessages(chatId: Long): Flow<List<Message>> {
    return messagesRepository.getMessages(chatId)
  }

  fun sendMessage(text: String, chat: Chat) {
    viewModelScope.launch {
      val chatId = chat.id!!
      val senderId = authRepository.getJid().first()
      val message = Message(
        text = text,
        chatId = chatId,
        senderId = senderId,
        timestamp = System.currentTimeMillis(),
      )
      messagesRepository.sendMessage(chat, message)
    }
  }
}