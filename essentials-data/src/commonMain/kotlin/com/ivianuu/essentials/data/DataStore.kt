/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.data

import com.ivianuu.essentials.optics.Lens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.updateAndGet

interface DataStore<T> {
  val data: Flow<T>

  suspend fun updateData(transform: T.() -> T): T
}

class TestDataStore<T>(initial: T) : DataStore<T> {
  override val data = MutableStateFlow(initial)

  override suspend fun updateData(transform: T.() -> T): T =
    data.updateAndGet(transform)
}

fun <T, S> DataStore<T>.lens(lens: Lens<T, S>): DataStore<S> = object : DataStore<S> {
  override val data: Flow<S>
    get() = this@lens.data.map { lens.get(it) }
  override suspend fun updateData(transform: S.() -> S): S =
    lens.get(this@lens.updateData { lens.set(this, transform(lens.get(this))) })
}
