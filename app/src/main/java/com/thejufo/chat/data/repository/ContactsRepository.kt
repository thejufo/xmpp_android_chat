package com.thejufo.chat.data.repository

import com.thejufo.chat.data.local.ContactsDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepository @Inject constructor(
  private val contactsDao: ContactsDao
) {

  fun getContacts() = contactsDao.getContacts()
}