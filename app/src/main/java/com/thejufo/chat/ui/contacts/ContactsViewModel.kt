package com.thejufo.chat.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.Contact
import com.thejufo.chat.data.repository.ChatsRepository
import com.thejufo.chat.data.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
  private val contactsRepository: ContactsRepository,
  private val chatsRepository: ChatsRepository,
) : ViewModel() {

  val contacts = contactsRepository.getContacts()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = emptyList())
  val chatCreated = MutableStateFlow<Chat?>(null)

  fun startConversation(jid: String) {
    viewModelScope.launch(Dispatchers.IO) {
      var chat = chatsRepository.getChatByRemoteId(jid)
      if (chat == null) {
        val chatId = chatsRepository.createChat(jid, false)
        chat = Chat(chatId, jid, false)
      }
      chatCreated.value = chat
    }
  }

  fun createGroup(participants: List<Contact>, name: String) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        chatsRepository.createGroup(participants, name)
        val chatId = chatsRepository.createChat(name, true)
        val chat = Chat(chatId, name, true)
        chatCreated.value = chat
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }
}