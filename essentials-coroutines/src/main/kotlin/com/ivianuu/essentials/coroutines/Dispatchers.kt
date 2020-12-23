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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.coroutines

import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

typealias DefaultDispatcher = CoroutineDispatcher

@Given
inline fun defaultDispatcher(): DefaultDispatcher = Dispatchers.Default

typealias MainDispatcher = CoroutineDispatcher

@Given
inline fun mainDispatcher(): MainDispatcher = Dispatchers.Main

typealias ImmediateMainDispatcher = CoroutineDispatcher

@Given
inline fun immediateMainDispatcher(): ImmediateMainDispatcher = Dispatchers.Main.immediate

typealias IODispatcher = CoroutineDispatcher

@Given
inline fun ioDispatcher(): IODispatcher = Dispatchers.IO
