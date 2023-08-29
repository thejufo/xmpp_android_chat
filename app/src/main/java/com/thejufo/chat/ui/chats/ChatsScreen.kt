package com.thejufo.chat.ui.chats

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thejufo.chat.R
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.ChatWithMessages
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.ui.theme.white
import com.thejufo.chat.ui.views.PopupMenu
import java.util.Random

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatsScreen(
  onChatClick: (Chat) -> Unit = {},
  onNewChatClick: () -> Unit = {},
  onNavigateToLogin: () -> Unit = {},
  viewModel: ChatsViewModel = hiltViewModel()
) {
  val chats by viewModel.chats.collectAsStateWithLifecycle()
  val navigateToLogin by viewModel.navigateToLogin.collectAsStateWithLifecycle()

  LaunchedEffect(navigateToLogin) {
    if (navigateToLogin) {
      onNavigateToLogin()
    }
  }

  LaunchedEffect(chats) {
    if (chats.isNotEmpty()) {
      viewModel.listenGroupMessages(chats.map { it.chat.chatName })
      Log.d("ChatsScreen", "Listening to group messages ${chats.size}")
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        elevation = 2.dp,
        backgroundColor = white,
        title = {
          Text(text = "Chats")
        },
        actions = {
          PopupMenu(
            onLogoutClick = { viewModel.logout() },
          )
        }
      )
    },
    floatingActionButton = {
      FloatingActionButton(onClick = onNewChatClick) {
        Icon(
          painter = painterResource(R.drawable.ic_plus),
          contentDescription = "New Chat"
        )
      }
    }
  ) { padding ->

    LazyColumn(
      modifier = Modifier.padding(padding),
      contentPadding = PaddingValues(vertical = 4.dp),
    ) {
      items(items = chats, key = { Random().nextInt() }) {

        ChatItem(
          chatWithMessages = it,
          onChatClick = onChatClick,
        )
      }
    }

    if (chats.isEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Image(
          painter = painterResource(R.drawable.empty),
          contentDescription = "",
          Modifier.width(96.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
          text = "No chats yet!\nYour chats will appear here",
          lineHeight = 24.sp,
          textAlign = TextAlign.Center,
          color = Color(0xFFA3A3A3),
          fontWeight = FontWeight.Normal,
          fontSize = 16.sp
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ChatItem(
  jid: String = "",
  modifier: Modifier = Modifier,
  chatWithMessages: ChatWithMessages,
  onChatClick: (Chat) -> Unit = {},
) {

  val lastMessage = chatWithMessages.messages.lastOrNull()
  val timestamp = lastMessage?.timestamp ?: 0
  val formattedTimestamp = DateUtils.getRelativeTimeSpanString(
    timestamp,
    System.currentTimeMillis(),
    DateUtils.DAY_IN_MILLIS,
    DateUtils.FORMAT_ABBREV_ALL
  ).toString()

  Card(
    modifier = modifier
      .fillMaxWidth(),
    elevation = 0.dp,
    onClick = { onChatClick(chatWithMessages.chat) }
  ) {

    Row(
      modifier = Modifier
        .height(72.dp)
        .padding(horizontal = 16.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Icon(
        modifier = Modifier.size(58.dp),
        tint = Color.Gray.copy(alpha = 0.3f),
        painter = painterResource(R.drawable.ic_account),
        contentDescription = ""
      )
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(text = chatWithMessages.chat.chatName, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Black, fontSize = 18.sp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically,

        ) {
          when (lastMessage?.status) {
            Message.Status.Failed -> {
              Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_failed),
                contentDescription = "",
                tint = Color.Red
              )
            }

            Message.Status.SENT -> {
              Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_sent),
                contentDescription = "",
                tint = Color.Gray.copy(alpha = 0.5f)
              )
            }

            Message.Status.SENDING -> {
              Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(R.drawable.ic_sending),
                contentDescription = "",
                tint = Color.Gray.copy(alpha = 0.5f)
              )
            }

            else -> {
              Box(Modifier)
            }
          }
          Text(
            text = lastMessage?.text ?: "",
            maxLines = 1,
            color = Color.Black.copy(alpha = 0.5f),
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Column(
        modifier = Modifier
          .fillMaxHeight()
          .padding(top = 6.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top
      ) {
        Text(
          text = formattedTimestamp,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = Color.Black.copy(alpha = 0.5f)
        )
      }
    }
  }
}


@Preview
@Composable
fun ChatItem() {
  ChatItem(
    chatWithMessages = ChatWithMessages(
      chat = Chat(
        id = 32,
        chatName = "324234",
        isGroupChat = false
      ),
      messages = listOf(
        Message(
          chatId = 0,
          senderId = "Taaxo",
          id = 32,
          text = "Hello",
          timestamp = 1693126620854L,
          status = Message.Status.SENDING
        )
      )
    )
  )
}