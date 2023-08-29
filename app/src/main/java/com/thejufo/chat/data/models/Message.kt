package com.thejufo.chat.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
  tableName = "messages",
  foreignKeys = [
    ForeignKey(
      entity = Chat::class,
      parentColumns = ["id"],
      childColumns = ["chatId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["chatId"])]
)
data class Message(

  @PrimaryKey(autoGenerate = true)
  var id: Long? = null,
  val chatId: Long,
  val senderId: String,
  val text: String,
  val status: Status = Status.SENDING,
  val timestamp: Long,
) : Parcelable {


  enum class Status {
    SENDING,
    SENT,
    RECEIVED,
    Failed,
  }
}