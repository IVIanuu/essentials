/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import com.ivianuu.injekt.common.TypeKey
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

interface Schema {
  val version: Int

  val embeddedFormat: StringFormat

  val serializersModule: SerializersModule

  val entities: List<EntityDescriptor<*>>

  suspend fun create(db: Db)

  suspend fun migrate(db: Db, from: Int, to: Int)

  context(TypeKey<T>) fun <T> descriptor(): EntityDescriptor<T>
}

fun Schema(
  version: Int,
  entities: List<EntityDescriptor<*>> = emptyList(),
  migrations: List<Migration> = emptyList(),
  serializersModule: SerializersModule = EmptySerializersModule,
  embeddedFormat: StringFormat = Json
): Schema = SchemaImpl(version, entities, migrations, serializersModule, embeddedFormat)

private class SchemaImpl(
  override val version: Int,
  override val entities: List<EntityDescriptor<*>>,
  private val migrations: List<Migration>,
  override val serializersModule: SerializersModule,
  override val embeddedFormat: StringFormat
) : Schema {
  private val _entities = entities.associateBy { it.typeKey.value }

  override suspend fun create(db: Db) {
    _entities.values
      .forEach { with(it) { db.createTable() } }
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

    migrationsToExecute.forEach { it.execute(db, from, to) }
  }

  context(TypeKey<T>) override fun <T> descriptor(): EntityDescriptor<T> =
    _entities[value]?.let { it as EntityDescriptor<T> } ?: error("Unknown entity ${this@TypeKey}")
}

interface Migration {
  val from: Int
  val to: Int

  suspend fun execute(db: Db, from: Int, to: Int)

  companion object {
    const val ANY_VERSION = -1
  }
}

inline fun Migration.before(crossinline action: suspend (Db, Int, Int) -> Unit): Migration =
  Migration(from, to) { db, from, to ->
    action(db, from, to)
    execute(db, from, to)
  }

inline fun Migration.after(crossinline action: suspend (Db, Int, Int) -> Unit): Migration =
  Migration(from, to) { db, from, to ->
    execute(db, from, to)
    action(db, from, to)
  }

inline fun Migration(
  from: Int = Migration.ANY_VERSION,
  to: Int = Migration.ANY_VERSION,
  crossinline execute: suspend (Db, Int, Int) -> Unit
) = object : Migration {
  override val from: Int
    get() = from

  override val to: Int
    get() = to

  override suspend fun execute(db: Db, from: Int, to: Int) {
    execute.invoke(db, from, to)
  }
}

fun DestructiveMigration(
  from: Int = Migration.ANY_VERSION,
  to: Int = Migration.ANY_VERSION
) = Migration(from, to) { db, _, _ ->
  db.dropAllAndRecreateTables()
}

fun DestructiveDowngradeMigration() = Migration { db, from, to ->
  if (from > to)
    db.dropAllAndRecreateTables()
}
