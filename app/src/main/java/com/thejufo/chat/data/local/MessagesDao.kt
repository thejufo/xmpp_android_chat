package com.thejufo.chat.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.thejufo.chat.data.models.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessagesDao {

  @Insert
  suspend fun insertMessage(message: Message): Long

  @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
  fun getMessages(chatId: Long): Flow<List<Message>>

  @Update(onConflict = REPLACE)
  suspend fun updateMessage(message: Message): Int

  @Delete
  suspend fun deleteMessage(message: Message)

  @Query("DELETE FROM messages")
  suspend fun deleteAll()
}