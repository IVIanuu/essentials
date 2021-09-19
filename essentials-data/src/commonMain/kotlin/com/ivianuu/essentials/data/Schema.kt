package com.ivianuu.essentials.data

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.common.TypeKey
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

interface Schema {
  val version: Int

  val embeddedFormat: StringFormat

  val serializersModule: SerializersModule

  suspend fun create(db: Db)

  suspend fun migrate(db: Db, from: Int, to: Int)

  fun <T> descriptor(@Inject key: TypeKey<T>): EntityDescriptor<T>
}

fun Schema(
  version: Int,
  entities: List<EntityDescriptor<*>> = emptyList(),
  migrations: List<Migration> = emptyList(),
  serializersModule: SerializersModule = SerializersModule {  },
  embeddedFormat: StringFormat = Json
): Schema = SchemaImpl(version, entities, migrations, serializersModule, embeddedFormat)

private class SchemaImpl(
  override val version: Int,
  entities: List<EntityDescriptor<*>>,
  private val migrations: List<Migration>,
  override val serializersModule: SerializersModule,
  override val embeddedFormat: StringFormat
) : Schema {
  private val entities = entities.associateBy { it.typeKey }

  override suspend fun create(db: Db) {
    entities.values
      .forEach { db.createTable(it) }
  }

  override suspend fun migrate(db: Db, from: Int, to: Int) {
    val migrationsToExecute = migrations
      .filter {
        (it.from == from || it.from == Migration.ANY_VERSION) &&
            (it.to == to || it.to == Migration.ANY_VERSION)
      }

    check(migrationsToExecute.isNotEmpty()) {
      "Could not find a migration from $from to $to"
    }

    migrationsToExecute.forEach { it.execute(db) }
  }

  override fun <T> descriptor(@Inject key: TypeKey<T>): EntityDescriptor<T> =
    entities[key]?.cast() ?: error("Unknown entity $key")
}

interface Migration {
  val from: Int
  val to: Int

  suspend fun execute(db: Db)

  companion object {
    const val ANY_VERSION = -1
  }
}

inline fun Migration.before(crossinline action: (Db) -> Unit): Migration = Migration(from, to) {
  action(it)
  execute(it)
}

inline fun Migration.after(crossinline action: (Db) -> Unit): Migration = Migration(from, to) {
  execute(it)
  action(it)
}

inline fun Migration(from: Int, to: Int, crossinline execute: suspend (Db) -> Unit) = object : Migration {
  override val from: Int
    get() = from

  override val to: Int
    get() = to

  override suspend fun execute(db: Db) {
    execute.invoke(db)
  }
}
