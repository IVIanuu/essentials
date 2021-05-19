package com.ivianuu.essentials.data

import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

interface DataStore<T> {
  val data: Flow<T>
  suspend fun updateData(transform: T.() -> T): T
}

@Provide fun <@Spread T : DataStore<D>, D> dataStoreFlow(dataStore: T): Flow<D> = dataStore.data
