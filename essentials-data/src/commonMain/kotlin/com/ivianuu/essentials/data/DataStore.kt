package com.ivianuu.essentials.data

import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

interface DataStore<T> {
  val data: Flow<T>
  suspend fun updateData(transform: T.() -> T): T
}

@Given fun <@Given T : DataStore<D>, D> dataStoreFlow(@Given dataStore: T): Flow<D> = dataStore.data
