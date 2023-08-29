package com.thejufo.chat.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(

  @PrimaryKey(autoGenerate = false)
  val jid: String
) {

  val name: String
    get() = jid.substringBefore('@')
}
