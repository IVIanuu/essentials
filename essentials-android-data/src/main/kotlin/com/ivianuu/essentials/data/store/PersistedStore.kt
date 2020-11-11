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
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreScope
import com.ivianuu.essentials.store.setStateIn
import com.ivianuu.essentials.store.store
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@FunBinding
inline fun <reified S, reified A> persistedStore(
    diskDataStoreFactory: DiskDataStoreFactory,
    globalScope: GlobalScope,
    @FunApi name: String,
    @FunApi initial: S,
    @FunApi noinline block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> = persistedStoreImpl(
    diskDataStoreFactory,
    globalScope,
    name,
    initial,
    typeOf<S>(),
    block
)

@PublishedApi
internal fun <S, A> persistedStoreImpl(
    diskDataStoreFactory: DiskDataStoreFactory,
    globalScope: GlobalScope,
    name: String,
    initial: S,
    type: KType,
    block: suspend StoreScope<S, A>.() -> Unit
): Store<S, A> {
    val dataStore = diskDataStoreFactory.create(name, type) { initial }
    return globalScope.store(initial) {
        dataStore.data.setStateIn(this) { it }
        val wrappedScope = object : StoreScope<S, A> by this {
            override suspend fun setState(block: suspend S.() -> S): S =
                dataStore.updateData(block)
        }
        block(wrappedScope)
    }
}
