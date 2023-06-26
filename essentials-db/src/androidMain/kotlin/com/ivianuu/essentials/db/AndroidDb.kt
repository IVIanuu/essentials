/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.bracket
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

class AndroidDb private constructor(
  override val schema: Schema,
  override val coroutineContext: CoroutineContext,
  private val openHelper: SQLiteOpenHelper?,
  database: SQLiteDatabase?
) : Db, CoroutineScope {
  override val tableChanges = EventFlow<String?>()

  private val database by lazy { database ?: openHelper!!.writableDatabase!! }

  init {
    checkNotNull(coroutineContext[ContinuationInterceptor])
  }

  constructor(
    name: String,
    schema: Schema,
    coroutineContext: CoroutineContext = Dispatchers.IO,
    // todo use context receiver once supported
    @Inject context: Context
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

      override fun onOpen(db: SQLiteDatabase) {
        db.enableWriteAheadLogging()
      }
    },
    null
  )

  override suspend fun <R> transaction(block: suspend Db.() -> R): R {
    val currentCoroutineContext = currentCoroutineContext()
    val transactionContext = currentCoroutineContext[TransactionContextKey] ?: run {
      val controlJob = Job()

      val job = currentCoroutineContext[Job]!!
      job.invokeOnCompletion { controlJob.cancel() }

      val interceptor = suspendCancellableCoroutine { cont ->
        cont.invokeOnCancellation { controlJob.cancel() }
        launch {
          runBlocking {
            cont.resume(coroutineContext[ContinuationInterceptor]!!)
            controlJob.join()
          }
        }
      }

      TransactionContext(interceptor, controlJob)
    }

    return withContext(transactionContext + transactionContext.interceptor) {
      transactionContext.acquire()
      database.beginTransactionNonExclusive()
      try {
        block()
          .also { database.setTransactionSuccessful() }
      } finally {
        database.endTransaction()
        transactionContext.release()
      }
    }
  }

  private inner class TransactionContext(
    val interceptor: ContinuationInterceptor,
    private val controlJob: Job
  ) : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*>
      get() = TransactionContextKey

    val changedTableNames = mutableSetOf<String?>()

    private var refs = 0
    private val refsLock = Mutex()

    suspend fun acquire() {
      refsLock.withLock { refs += 1 }
    }

    suspend fun release() {
      if (refsLock.withLock { refs -= 1; refs } == 0) {
        controlJob.cancel()
        this@AndroidDb.launch {
          synchronized(changedTableNames) { changedTableNames.toList() }
            .forEach { tableChanges.emit(it) }
        }
      }
    }
  }

  private object TransactionContextKey : CoroutineContext.Key<TransactionContext>

  override suspend fun execute(sql: String, tableName: String?) =
    withTransactionOrDefaultContext { database.execSQL(sql) }
      .also { handleTableMutation(tableName) }

  override suspend fun executeInsert(sql: String, tableName: String?): Long =
    withTransactionOrDefaultContext { database.compileStatement(sql).executeInsert() }
      .also { handleTableMutation(tableName) }

  private suspend fun <R> withTransactionOrDefaultContext(block: suspend CoroutineScope.() -> R): R =
    withContext(
      currentCoroutineContext()[TransactionContextKey]?.interceptor ?: coroutineContext,
      block
    )

  private suspend fun handleTableMutation(tableName: String?) {
    currentCoroutineContext()[TransactionContextKey]
      ?.let {
        synchronized(it.changedTableNames) {
          it.changedTableNames += tableName
        }
      } ?: tableChanges.emit(tableName)
  }

  override fun <T> query(sql: String, tableName: String?, transform: (Cursor) -> T): Flow<T> =
    tableChanges
      .filter { tableName == null || it == tableName }
      .onStart { emit(tableName) }
      .map {
        withTransactionOrDefaultContext {
          bracket(
            acquire = { AndroidCursor(database.rawQuery(sql, null)) },
            use = { transform(it) },
            release = { cursor, _ -> cursor.dispose() }
          )
        }
      }
      .distinctUntilChanged()

  override fun dispose() {
    openHelper?.close() ?: database.close()
  }
}

@Provide class AndroidDbFactory(
  private val context: Context,
  private val coroutineContexts: CoroutineContexts
) : DbFactory {
  override fun invoke(name: String, schema: Schema) = AndroidDb(
    name = name,
    schema = schema,
    coroutineContext = coroutineContexts.io,
    context = context
  )
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
