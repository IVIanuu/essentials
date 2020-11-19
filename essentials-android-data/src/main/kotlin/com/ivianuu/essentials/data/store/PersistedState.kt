/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.data.store

import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.datastore.disk.DiskDataStoreFactory
import com.ivianuu.essentials.store.StateScope
import com.ivianuu.essentials.store.state
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@FunBinding
inline fun <reified S> persistedState(
    diskDataStoreFactory: DiskDataStoreFactory,
    globalScope: GlobalScope,
    @FunApi name: String,
    @FunApi initial: S,
    @FunApi noinline block: StateScope<S>.() -> Unit
): StateFlow<S> = persistedStateImpl(
    diskDataStoreFactory,
    globalScope,
    name,
    initial,
    typeOf<S>(),
    block
)

@PublishedApi
internal fun <S> persistedStateImpl(
    diskDataStoreFactory: DiskDataStoreFactory,
    globalScope: GlobalScope,
    name: String,
    initial: S,
    type: KType,
    block: StateScope<S>.() -> Unit
): StateFlow<S> {
    val dataStore = diskDataStoreFactory.create(name, type) { initial }
    return globalScope.state(
        initial,
        dataStore.data,
        { newState -> dataStore.updateData { newState } },
        object : SharingStarted {
            override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> =
                dataStore.data.map { SharingCommand.START }
        },
        block
    )
}
