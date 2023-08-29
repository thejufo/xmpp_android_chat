package com.thejufo.chat.ui.conversation

import android.text.format.DateUtils
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thejufo.chat.R
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.icon
import com.thejufo.chat.ui.theme.ChatTheme
import com.thejufo.chat.ui.theme.black
import com.thejufo.chat.ui.theme.blue
import com.thejufo.chat.ui.theme.skyBlue
import com.thejufo.chat.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.reflect.KFunction2

@Composable
fun ConversationScreen(
  chat: Chat,
  onNavigateUp: () -> Unit = {},
  viewModel: ConversationViewModel = hiltViewModel(),
) {
  val messages by viewModel.getMessages(chat.id ?: -1).collectAsStateWithLifecycle(emptyList())
  val currentUserJid by viewModel.currentUserJid.collectAsStateWithLifecycle()

  ConversationScreen(
    chat = chat,
    messages = messages,
    currentUserJid = currentUserJid,
    onSendMessage = { viewModel.sendMessage(it, chat) },
    onNavigateUp = onNavigateUp,
  )
}

@Composable
fun ConversationScreen(
  chat: Chat,
  messages: List<Message> = emptyList(),
  currentUserJid: String = "",
  onSendMessage: (String) -> Unit = {},
  onNavigateUp: () -> Unit = {},
) {

  val scrollState = rememberLazyListState()

  LaunchedEffect(messages) {
    delay(50)
    scrollState.animateScrollToItem(messages.size)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.background,
        title = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              modifier = Modifier.size(46.dp),
              tint = Color.Black.copy(alpha = 0.25f),
              painter = painterResource(R.drawable.ic_account),
              contentDescription = ""
            )
            Column(
              verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
              Text(text = chat.chatName, fontSize = 18.sp, fontWeight = FontWeight.Normal)
              Text(
                text = "Last seen recently",
                letterSpacing = 0.4.sp,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray.copy(alpha = 0.8f)
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onNavigateUp) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back"
            )
          }
        }
      )
    },
  ) { padding ->
    Column(
      Modifier
        .padding(padding)
        .fillMaxSize()
        .background(color = Color(0xFFf4f7fa))
    ) {
      LazyColumn(
        Modifier
          .weight(1f)
          .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.Bottom,
        state = scrollState
      ) {
        items(messages, key = { it.id!! }) { message ->
          val isMyMessage = currentUserJid == message.senderId
          CompositionLocalProvider(LocalLayoutDirection provides if (isMyMessage) LayoutDirection.Ltr else LayoutDirection.Rtl) {

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
              horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = if (isMyMessage) Alignment.End else Alignment.End
              ),
              verticalAlignment = Alignment.CenterVertically
            ) {
              val formatDate = SimpleDateFormat("h:M a", Locale.US).format(message.timestamp)
              Text(text = formatDate, color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp)
              Surface(
                elevation = if (isMyMessage) 0.dp else 1.dp,
                color = if (isMyMessage) Color(0xFF4986f7) else white,
                shape = RoundedCornerShape(16.dp).copy(bottomEnd = CornerSize(0.dp)),
              ) {
                Row(
                  modifier = Modifier
                    .widthIn(0.dp, 200.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  verticalAlignment = Alignment.Bottom
                ) {
                  Text(
                    message.text,
                    color = if (isMyMessage) white else black,
                    fontSize = 16.sp
                  )
                  if (isMyMessage) {
                    Icon(
                      painter = painterResource(message.icon()),
                      tint = Color.White.copy(alpha = 0.7f),
                      modifier = Modifier.size(16.dp),
                      contentDescription = ""
                    )
                  }
                }
              }
            }
          }
        }
      }

      Divider()
      ConversationEntry(onSendMessage)
    }
  }
}

@Composable
fun ConversationEntry(
  onSendMessage: (String) -> Unit = {},
) {

  var message by remember { mutableStateOf("") }

  Row(
    Modifier
      .fillMaxWidth()
      .background(color = white)
      .padding(horizontal = 4.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {

    IconButton(
      enabled = false,
      onClick = { onSendMessage(message) }
    ) {
      Icon(
        modifier = Modifier.size(28.dp),
        painter = painterResource(R.drawable.ic_plus),
        contentDescription = "",
        tint = Color.Gray,
      )
    }

    BasicTextField(
      value = message,
      onValueChange = {
        message = it
      },
      modifier = Modifier
        .weight(1f)
        .background(Color.Gray.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp))
        .padding(horizontal = 16.dp, vertical = 10.dp),
      maxLines = 3,
      textStyle = TextStyle(
        fontSize = 18.sp,
        letterSpacing = 0.15.sp,
      ),
      decorationBox = { innerTextField ->
        Row(
          Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(Modifier.weight(1f)) {
            if (message.isEmpty()) Text(
              "Message",
              fontSize = 18.sp,
              letterSpacing = 0.15.sp,
              style = LocalTextStyle.current.copy(
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
              )
            )
            innerTextField()
          }
        }
      }
    )

    IconButton(
      enabled = message.isNotBlank(),
      onClick = {
        onSendMessage(message)
        message = ""
      }
    ) {
      Icon(
        painter = painterResource(R.drawable.ic_send),
        tint = if (message.isNotBlank()) MaterialTheme.colors.primary else Color.Gray,
        contentDescription = "Send"
      )
    }
  }
}

//@Preview
//@Composable
//fun ConversationScreenPreview() {
//  ChatTheme {
//    ConversationScreen(
//      chat = Chat(contactId = "test"),
//    )
//  }
//}