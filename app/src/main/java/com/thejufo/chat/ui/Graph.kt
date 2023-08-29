package com.thejufo.chat.ui

import android.os.Bundle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.thejufo.chat.data.models.Chat

sealed class Graph(val route: String, val arguments: List<NamedNavArgument> = emptyList()) {
  object Auth : Graph("/auth")
  object CHATS : Graph("/chats")
  object Contacts : Graph("/contacts")
  object Conversation : Graph(
    "/conversation/{chat}",
    listOf(
      navArgument("chat") {
        type = ChatParamType()
      },
    )
  ) {
    fun createGraph(chat: String): String {
      return "/conversation/$chat"
    }
  }
}


class ChatParamType : NavType<Chat>(false) {
  override fun get(bundle: Bundle, key: String): Chat? {
    return bundle.getParcelable(key)
  }

  override fun parseValue(value: String): Chat {
    return Gson().fromJson(value, Chat::class.java)
  }

  override fun put(bundle: Bundle, key: String, value: Chat) {
    bundle.putParcelable(key, value)
  }
}