/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import com.ivianuu.injekt.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun interface Identifiable<T, I> {
  fun id(x: T): I
}

fun <T, I> DataStore<List<T>>.item(id: I, @Inject identifiable: Identifiable<T, I>): Flow<T?> = data
  .map { items -> items.firstOrNull { identifiable.id(it) == id } }

suspend fun <T> DataStore<List<T>>.put(vararg items: T, @Inject identifiable: Identifiable<T, *>) {
  updateData {
    toMutableList().apply {
      for (item in items) {
        val itemId = identifiable.id(item)
        val index = indexOfFirst { identifiable.id(it) == itemId }
        if (index == -1) add(item)
        else {
          removeAt(index)
          add(index, item)
        }
      }
    }
  }
}

suspend fun <T, I> DataStore<List<T>>.delete(
  vararg ids: I,
  @Inject identifiable: Identifiable<T, I>
) {
  updateData { filter { identifiable.id(it) !in ids } }
}
