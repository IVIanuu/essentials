package com.ivianuu.essentials.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ivianuu.essentials.coroutines.EventFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

private val databaseFile = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

class AndroidDb private constructor(
  override val schema: Schema,
  private val coroutineContext: CoroutineContext,
  private val openHelper: SQLiteOpenHelper?,
  database: SQLiteDatabase?
) : Db {
  private val transactionsMutex = Mutex()
  private var currentTransaction: TransactionImpl? = null

  private val changes = EventFlow<Unit>()

  private val database by lazy { database ?: openHelper!!.writableDatabase!! }

  constructor(
    context: Context,
    name: String,
    schema: Schema,
    coroutineContext: CoroutineContext = databaseFile
  ) : this(
    schema,
    coroutineContext,
    object : SQLiteOpenHelper(
      context,
      name,
      null,
      schema.version
    ) {
      override fun onCreate(db: SQLiteDatabase) {
        runBlocking {
          schema.create(AndroidDb(schema, this.coroutineContext, null, db))
        }
      }

      override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        runBlocking {
          schema.migrate(AndroidDb(schema, this.coroutineContext, null, db), oldVersion, newVersion)
        }
      }

      override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        runBlocking {
          schema.migrate(AndroidDb(schema, this.coroutineContext, null, db), oldVersion, newVersion)
        }
      }
    },
    null
  )

  override suspend fun currentTransaction(): Transaction? =
    transactionsMutex.withLock { currentTransaction }

  override suspend fun beginTransaction(): Transaction {
    val transaction = transactionsMutex.withLock {
      TransactionImpl(currentTransaction)
        .also { currentTransaction = it }
    }

    if (transaction.parent == null) {
      withContext(coroutineContext) {
        database.beginTransactionNonExclusive()
      }
    }

    return transaction
  }

  private inner class TransactionImpl(val parent: TransactionImpl?) : Transaction {
    private var childrenSuccessful = true

    override suspend fun endTransaction(successful: Boolean) {
      transactionsMutex.withLock {
        currentTransaction = parent
        if (parent == null) {
          withContext(coroutineContext) {
            if (successful && childrenSuccessful)
              database.setTransactionSuccessful()
            database.endTransaction()
          }

          if (successful && childrenSuccessful)
            changes.emit(Unit)
        } else {
          parent.childrenSuccessful = parent.childrenSuccessful && successful
        }
      }
    }
  }

  override suspend fun execute(sql: String) {
    withContext(coroutineContext) {
      database.execSQL(sql)
    }
  }

  override fun <T> query(sql: String, transform: (Cursor) -> T): Flow<T> = changes
    .onStart { emit(Unit) }
    .map {
      val cursor = AndroidCursor(database.rawQuery(sql, null))
      try {
        transform(cursor)
      } finally {
        cursor.dispose()
      }
    }
    .distinctUntilChanged()
    .flowOn(coroutineContext.minusKey(Job))

  override fun dispose() {
    database.close()
  }
}

private class AndroidCursor(private val cursor: android.database.Cursor) : Cursor {
  override fun next() = cursor.moveToNext()

  override fun getColumnIndex(name: String): Int = cursor.getColumnIndex(name)

  override fun isNull(index: Int): Boolean = cursor.isNull(index)

  override fun getString(index: Int) = if (cursor.isNull(index)) null else cursor.getString(index)

  override fun getLong(index: Int) = if (cursor.isNull(index)) null else cursor.getLong(index)

  override fun getBytes(index: Int) = if (cursor.isNull(index)) null else cursor.getBlob(index)

  override fun getDouble(index: Int) = if (cursor.isNull(index)) null else cursor.getDouble(index)

  override fun dispose() = cursor.close()
}
