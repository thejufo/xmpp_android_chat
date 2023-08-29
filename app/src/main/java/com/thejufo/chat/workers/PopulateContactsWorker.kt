package com.thejufo.chat.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thejufo.chat.data.local.db.AppDatabase
import com.thejufo.chat.data.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PopulateContactsWorker(
  context: Context,
  params: WorkerParameters
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    try {

      val contacts = listOf(
        "900573735692@uatchat2.waafi.com",
        "900556231066@uatchat2.waafi.com",
        "900520971775@uatchat2.waafi.com",
        "900412933183@uatchat2.waafi.com",
        "900330404771@uatchat2.waafi.com",
        "900142447532@uatchat2.waafi.com",
        "900184827789@uatchat2.waafi.com",
        "900588173208@uatchat2.waafi.com",
        "900558978697@uatchat2.waafi.com",
        "900440548563@uatchat2.waafi.com",
        "900369095483@uatchat2.waafi.com",
        "900147240839@uatchat2.waafi.com",
        "900228932315@uatchat2.waafi.com",
        "901327542282@uatchat2.waafi.com",
        "901305975729@uatchat2.waafi.com",
        "900733366848@uatchat2.waafi.com",
        "900748400611@uatchat2.waafi.com",
        "900758690731@uatchat2.waafi.com",
        "901092505722@uatchat2.waafi.com",
        "901069872661@uatchat2.waafi.com"
      ).map { Contact(jid = it) }

      val db = AppDatabase.getInstance(applicationContext)
      val contactsDao = db.contactsDao()
      contactsDao.upsertAll(contacts)

      Log.d(TAG, "Populating contacts ${contacts.size}: Success")
      Result.success()
    } catch (e: Exception) {
      Log.e(TAG, "Error populating contacts")
      Result.failure()
    }
  }

  companion object {
    private const val TAG = "PopulateContactsWorker"
  }
}