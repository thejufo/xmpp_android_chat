package com.thejufo.chat.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.thejufo.chat.data.models.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

  @Query("SELECT * FROM contacts")
  fun getContacts(): Flow<List<Contact>>

  @Upsert
  suspend fun upsertAll(contacts: List<Contact>)
}