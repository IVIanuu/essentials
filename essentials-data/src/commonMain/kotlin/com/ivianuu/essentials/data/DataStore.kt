package com.ivianuu.essentials.data

import kotlinx.coroutines.flow.Flow

interface DataStore<T> : Flow<T> {
    suspend fun update(transform: T.() -> T): T
    fun dispatchUpdate(transform: T.() -> T)
}
