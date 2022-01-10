/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.data

import kotlinx.coroutines.flow.*

interface DataStore<T> {
  val data: Flow<T>

  suspend fun updateData(transform: T.() -> T): T
}

class TestDataStore<T>(initial: T) : DataStore<T> {
  override val data = MutableStateFlow(initial)

  override suspend fun updateData(transform: T.() -> T): T =
    data.updateAndGet(transform)
}
