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

package com.ivianuu.essentials.store

import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.DiskDataStoreFactory
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Binding
fun <S, A> CoroutineScope.storeFromDataStore(
    dataStore: DataStore<S>,
    block: suspend StoreScope<S, A>.() -> Unit
) = storeFromSource(
    state = dataStore.data.stateIn(this, SharingStarted.Eagerly, dataStore.defaultData),
    setState = { newState -> dataStore.updateData { newState } },
    block = block
)

@FunBinding
inline fun <reified S, reified A> dataStore(
    globalScope: GlobalScope,
    dataStoreFactory: DiskDataStoreFactory,
    name: @Assisted String,
    initial: @Assisted S,
    noinline block: @Assisted suspend StoreScope<S, A>.() -> Unit
) = globalScope.storeFromDataStore(
    dataStore = dataStoreFactory.create(name) { initial },
    block = block
)
