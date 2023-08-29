package com.thejufo.chat.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.thejufo.chat.animatedComposable
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.ui.auth.AuthScreen
import com.thejufo.chat.ui.chats.ChatsScreen
import com.thejufo.chat.ui.contacts.ContactsScreen
import com.thejufo.chat.ui.conversation.ConversationScreen

@Composable
fun NavigationGraph() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = Graph.Auth.route) {

    // Authentication Screen
    animatedComposable(Graph.Auth) {
      AuthScreen(
        onAuthSuccess = {
          navController.navigate(route = Graph.CHATS.route, builder = {
            popUpTo(Graph.Auth.route) { inclusive = true }
          })
        })
    }

    // Chats Screen
    animatedComposable(Graph.CHATS) {
      ChatsScreen(
        onChatClick = {
          val json = Uri.encode(Gson().toJson(it))
          navController.navigate(Graph.Conversation.createGraph(json))
        },
        onNewChatClick = {
          navController.navigate(Graph.Contacts.route)
        },
        onNavigateToLogin = {
          navController.navigate(Graph.Auth.route, builder = {
            popUpTo(Graph.CHATS.route) { inclusive = true }
          })
        }
      )
    }

    // Contacts Screen
    animatedComposable(Graph.Contacts) {
      ContactsScreen(
        onStartChat = {
          val json = Uri.encode(Gson().toJson(it))
          navController.navigate(Graph.Conversation.createGraph(json), builder = {
            popUpTo(Graph.Contacts.route) { inclusive = true }
          })
        },
        onNavigateUp = {
          navController.navigateUp()
        },
      )
    }

    // Conversation Screen
    animatedComposable(Graph.Conversation) {
      val chat = it.arguments?.getParcelable<Chat>("chat")
      ConversationScreen(
        chat = chat!!,
        onNavigateUp = {
          navController.navigateUp()
        }
      )
    }
  }
}