/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.db

import com.ivianuu.injekt.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import kotlin.reflect.*

class Schema(
  val version: Int,
  val entities: List<EntityDescriptor<*>> = emptyList(),
  private val migrations: List<Migration> = emptyList(),
  val serializersModule: SerializersModule = EmptySerializersModule(),
  val embeddedFormat: StringFormat = Json,
) {
  private val _entities = entities.associateBy { it.key }

  suspend fun create(db: Db) {
    _entities.values
      .forEach { db.createTable(it) }
  }

  suspend fun migrate(db: Db, from: Int, to: Int) {
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

  fun <T : Any> descriptor(key: KClass<T> = inject): EntityDescriptor<T> =
    _entities[key]?.let { it as EntityDescriptor<T> } ?: error("Unknown entity $key")
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
