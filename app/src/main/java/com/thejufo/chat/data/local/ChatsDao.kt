package com.thejufo.chat.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.thejufo.chat.data.models.Chat
import com.thejufo.chat.data.models.ChatWithMessages
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatsDao {

  @Insert
  suspend fun insertChat(chat: Chat): Long

  @Transaction
  @Query(
    """
      SELECT *
      FROM messages
      JOIN chats
      ON chats.id = messages.chatId
      WHERE timestamp = (
        SELECT max(timestamp) FROM messages WHERE chatId = chats.id
      )
      ORDER BY messages.timestamp DESC
    """
  )
  fun getChatsWithLastMessage(): Flow<List<ChatWithMessages>>

  @Query("SELECT * FROM chats WHERE id = :id LIMIT 1")
  suspend fun getChat(id: Int): Chat?

  // Get chat by remoteId::either mucJid or or p2pJid
  // Usually chatName is the remoteId without the domain part
  // e.g. "user@example.com" chatName is "user"
  @Query("SELECT * FROM chats WHERE chatName = :remoteId LIMIT 1")
  suspend fun getChatByRemoteId(remoteId: String): Chat?

  @Delete
  suspend fun deleteChat(chat: Chat)

  @Query("DELETE FROM chats")
  suspend fun deleteAll()
}