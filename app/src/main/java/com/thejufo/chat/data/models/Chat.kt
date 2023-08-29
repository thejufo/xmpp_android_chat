package com.thejufo.chat.data.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "chats")
data class Chat(

  @PrimaryKey(autoGenerate = true)
  var id: Long? = null,
  val chatName: String,
  val isGroupChat: Boolean,
) : Parcelable

@Parcelize
data class ChatWithMessages(
  @Embedded
  val chat: Chat,

  @Relation(
    parentColumn = "id",
    entityColumn = "chatId"
  )
  val messages: List<Message>
) : Parcelable
