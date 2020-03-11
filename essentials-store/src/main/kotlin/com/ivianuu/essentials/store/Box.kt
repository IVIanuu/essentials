/*
 * Copyright 2019 Manuel Wrage
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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface Box<T> {

    val defaultValue: T
    val isDisposed: Boolean
    val value: Flow<T>

    suspend fun update(value: T)

    fun dispose()
}

suspend fun <T> Box<T>.getValue(): T = value.first()
