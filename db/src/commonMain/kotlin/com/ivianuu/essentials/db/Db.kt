/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.*
import kotlin.reflect.*

interface Db : Disposable {
  val schema: Schema

  val coroutineContext: CoroutineContext

  val tableChanges: Flow<String?>

  suspend fun <R> transaction(block: suspend Db.() -> R): R

  suspend fun execute(sql: String, tableName: String?)

  suspend fun executeInsert(sql: String, tableName: String?): Long

  fun <T> query(sql: String, tableName: String?, transform: (Cursor) -> T): Flow<T>
}

fun <T : Any> Db.query(
  sql: String,
  tableName: String?,
  key: KClass<T> = inject
): Flow<List<T>> = query(sql, tableName) { it.toList(schema) }

suspend fun <T : Any> Db.insert(
  entity: T,
  conflictStrategy: InsertConflictStrategy = InsertConflictStrategy.ABORT,
  key: KClass<T> = inject
): Long {
  val descriptor = schema.descriptor<T>()
  return executeInsert(
    "INSERT ${
      when (conflictStrategy) {
        InsertConflictStrategy.REPLACE -> "OR REPLACE "
        InsertConflictStrategy.ABORT -> "OR ABORT "
        InsertConflictStrategy.IGNORE -> "OR IGNORE "
      }
    }INTO ${descriptor.tableName} ${entity.toSqlColumnsAndArgsString(schema)}",
    descriptor.tableName
  )
}

suspend fun <T : Any> Db.insertAll(
  entities: List<T>,
  conflictStrategy: InsertConflictStrategy = InsertConflictStrategy.ABORT,
  key: KClass<T> = inject
) {
  entities.forEach { insert(it, conflictStrategy) }
}

enum class InsertConflictStrategy { REPLACE, ABORT, IGNORE }

fun <T : Any> Db.selectAll(key: KClass<T> = inject): Flow<List<T>> {
  val descriptor = schema.descriptor<T>()
  return query(
    "SELECT * FROM ${descriptor.tableName}",
    descriptor.tableName
  )
}

fun <T : Any> Db.selectById(id: Any, key: KClass<T> = inject): Flow<T?> {
  val descriptor = schema.descriptor<T>()
  val primaryKeyRow = descriptor.rows.single { it.isPrimaryKey }
  return query<T>(
    "SELECT * FROM ${descriptor.tableName} " +
        "WHERE ${primaryKeyRow.name} = ${id.toSqlArg(primaryKeyRow)}",
    descriptor.tableName
  )
    .map { it.singleOrNull() }
}

fun <T : Any, S> Db.selectAllTransform(
  key: KClass<T> = inject,
  transform: suspend (T?) -> S?
): Flow<List<S>> = tableChanges
  .onStart { emit(null) }
  .mapLatest {
    selectAll<T>()
      .first()
      .mapNotNull { transform(it) }
  }
  .distinctUntilChangedBy { it.hashCode() }

fun <T : Any, S> Db.selectByIdTransform(
  id: Any,
  key: KClass<T> = inject,
  transform: suspend (T?) -> S
): Flow<S> = tableChanges
  .onStart { emit(null) }
  .mapLatest { transform(selectById<T>(id).first()) }
  .distinctUntilChanged()

suspend fun <T : Any> Db.deleteById(vararg ids: Any, key: KClass<T> = inject) {
  val descriptor = schema.descriptor<T>()
  val primaryKeyRow = descriptor.rows.single { it.isPrimaryKey }
  execute(
    "DELETE FROM ${descriptor.tableName} " +
        "WHERE ${primaryKeyRow.name} in (${ids.joinToString { it.toSqlArg(primaryKeyRow) }})",
    descriptor.tableName,
  )
}

suspend fun <T : Any> Db.deleteAll(key: KClass<T> = inject) {
  val tableName = schema.descriptor<T>().tableName
  execute("DELETE FROM $tableName", tableName)
}

interface Cursor : Disposable {
  fun next(): Boolean

  fun isNull(index: Int): Boolean

  fun getString(index: Int): String?

  fun getLong(index: Int): Long

  fun getLongOrNull(index: Int): Long?

  fun getBytes(index: Int): ByteArray?

  fun getDouble(index: Int): Double

  fun getDoubleOrNull(index: Int): Double?

  fun getColumnIndex(name: String): Int
}

fun <T : Any> Cursor.toList(schema: Schema, key: KClass<T> = inject): List<T> = buildList {
  while (next()) {
    val serializer = schema.descriptor<T>().serializer
    this += serializer.deserialize(
      CursorDecoder(
        this@toList,
        schema.serializersModule,
        serializer.descriptor,
        schema.embeddedFormat
      )
    )
  }
  dispose()
}

fun Db.tableNames(): Flow<List<String>> =
  query("SELECT * FROM sqlite_master WHERE type='table';", "sqlite_master") { cursor ->
    buildList {
      while (cursor.next()) {
        val tableName = cursor.getString(1)!!
        if (tableName != "android_metadata" && tableName != "sqlite_sequence")
          add(tableName)
      }
    }
  }

suspend fun <T : Any> Db.createTable(
  descriptor: EntityDescriptor<T> = inject,
  tableName: String = descriptor.tableName
) {
  execute(
    sql = buildString {
      append("CREATE TABLE IF NOT EXISTS $tableName")
      append("(")
      descriptor.rows.forEachIndexed { index, row ->
        append(row.name)

        when (row.type) {
          Row.Type.STRING -> append(" TEXT")
          Row.Type.INT -> append(" INTEGER")
          Row.Type.BYTES -> append(" BLOB")
          Row.Type.DOUBLE -> append(" REAL")
        }

        if (row.isPrimaryKey)
          append(" PRIMARY KEY")

        if (row.autoIncrement)
          append(" AUTOINCREMENT")

        if (!row.isNullable)
          append(" NOT NULL")

        if (index != descriptor.rows.lastIndex)
          append(",")
      }
      append(")")
    },
    tableName = tableName
  )

  descriptor.indices.forEach { createIndex(it, descriptor.tableName) }
}

suspend fun Db.dropTable(tableName: String) {
  execute("DROP TABLE IF EXISTS $tableName", tableName)
}

suspend fun Db.dropAllAndRecreateTables() {
  tableNames().first().forEach { dropTable(it) }
  schema.entities.forEach { createTable(it) }
}

suspend fun Db.createIndex(index: Index, tableName: String) {
  execute(
    sql = buildString {
      append("CREATE ")
      if (index.unique)
        append("UNIQUE ")
      append("INDEX IF NOT EXISTS ${index.name} ON $tableName")
      append(index.columnNames.joinToString(", ", "(", ")"))
    },
    tableName = tableName
  )
}

suspend fun Db.dropIndex(indexName: String, tableName: String) {
  execute("DROP INDEX IF EXISTS $indexName", tableName)
}

fun interface DbFactory {
  fun createDb(name: String, schema: Schema): Db
}
