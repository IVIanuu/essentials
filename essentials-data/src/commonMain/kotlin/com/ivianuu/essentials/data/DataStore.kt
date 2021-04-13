package com.ivianuu.essentials.data

import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow

interface DataStore<T> {
    val data: Flow<T>
    suspend fun updateData(transform: T.() -> T): T
}

@Given
fun <@Given T : DataStore<D>, D> dataStoreFlow(@Given dataStore: T): Flow<D> = dataStore.data
