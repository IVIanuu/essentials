package com.ivianuu.essentials.data

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.Disposable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface Db : Disposable {
  val schema: Schema

  suspend fun currentTransaction(): Transaction?

  suspend fun beginTransaction(): Transaction

  suspend fun execute(sql: String)

  fun <T> query(sql: String, transform: (Cursor) -> T): Flow<T>
}

suspend inline fun <R> Db.transaction(block: () -> R): R {
  val transaction = beginTransaction()
  return try {
    block()
      .also { transaction.endTransaction(true) }
  } catch (e: Throwable) {
    transaction.endTransaction(false)
    throw e
  }
}

fun <T> Db.query(sql: String, @Inject key: TypeKey<T>): Flow<List<T>> =
  query(sql) { it.toList(schema) }

suspend fun <T> Db.insert(
  entity: T,
  conflictStrategy: InsertConflictStrategy = InsertConflictStrategy.ABORT,
  @Inject key: TypeKey<T>
) = transaction {
  val descriptor = schema.descriptor<T>()
  execute("INSERT ${when (conflictStrategy) {
    InsertConflictStrategy.REPLACE -> " OR REPLACE "
    InsertConflictStrategy.ABORT -> " OR ABORT "
    InsertConflictStrategy.IGNORE -> " OR IGNORE "
  }}INTO ${descriptor.tableName} ${entity.toSqlColumnsAndArgsString(schema)}")
}

suspend fun <T> Db.insertAll(
  entities: List<T>,
  conflictStrategy: InsertConflictStrategy = InsertConflictStrategy.ABORT,
  @Inject key: TypeKey<T>
) = transaction {
  entities.forEach { insert(it, conflictStrategy) }
}

enum class InsertConflictStrategy {
  REPLACE,
  ABORT,
  IGNORE
}

fun <T> Db.selectAll(@Inject key: TypeKey<T>): Flow<List<T>> {
  val descriptor = schema.descriptor<T>()
  return query<T>("SELECT * FROM ${descriptor.tableName}")
}

fun <T> Db.selectById(id: Any, @Inject key: TypeKey<T>): Flow<T?> {
  val descriptor = schema.descriptor<T>()
  val primaryKeyRow = descriptor.rows.single { it.isPrimaryKey }
  return query<T>("SELECT * FROM ${descriptor.tableName} " +
      "WHERE ${primaryKeyRow.name} = ${id.toSqlArg(primaryKeyRow)}")
    .map { it.singleOrNull() }
}

suspend fun <T> Db.deleteById(vararg ids: Any, @Inject key: TypeKey<T>) = transaction {
  val descriptor = schema.descriptor<T>()
  val primaryKeyRow = descriptor.rows.single { it.isPrimaryKey }
  execute("DELETE FROM ${descriptor.tableName} " +
      "WHERE ${primaryKeyRow.name} in (${ids.joinToString { it.toSqlArg(primaryKeyRow) }})")
}

suspend fun <T> Db.deleteAll(@Inject key: TypeKey<T>) = transaction {
  val descriptor = schema.descriptor<T>()
  execute("DELETE FROM ${descriptor.tableName}")
}

interface Transaction {
  suspend fun endTransaction(successful: Boolean)
}

interface Cursor : Disposable {
  fun next(): Boolean

  fun isNull(index: Int): Boolean

  fun getString(index: Int): String?

  fun getLong(index: Int): Long?

  fun getBytes(index: Int): ByteArray?

  fun getDouble(index: Int): Double?

  fun getColumnIndex(name: String): Int
}

@OptIn(ExperimentalStdlibApi::class) fun <T> Cursor.toList(
  schema: Schema,
  @Inject key: TypeKey<T>
): List<T> = buildList {
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

suspend fun <T> Db.createTable(
  @Inject entity: EntityDescriptor<T>,
  tableName: String = entity.tableName
) {
  execute(
    buildString {
      append("CREATE TABLE IF NOT EXISTS $tableName")
      append("(")
      entity.rows.forEachIndexed { index, row ->
        append(row.name)

        when (row.type) {
          Row.Type.STRING -> append(" TEXT")
          Row.Type.LONG -> append(" LONG")
          Row.Type.BYTES -> append(" BLOB")
          Row.Type.DOUBLE -> append(" REAL")
        }

        if (row.isPrimaryKey)
          append(" PRIMARY KEY")

        if (!row.isNullable)
          append(" NOT NULL")

        if (index != entity.rows.lastIndex)
          append(",")
      }
      append(")")
    }
  )
}
