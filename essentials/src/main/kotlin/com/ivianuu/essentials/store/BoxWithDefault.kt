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

interface BoxWithDefault<T> : Box<T> {
    val defaultValue: T
    override suspend fun get(): T
}

fun <T> Box<T>.asBoxWithDefault(
    defaultValue: T
): BoxWithDefault<T> = BoxWithDefaultImpl(defaultValue, this)

internal class BoxWithDefaultImpl<T>(
    override val defaultValue: T,
    private val wrapped: Box<T>
) : Box<T> by wrapped, BoxWithDefault<T> {

    override suspend fun get(): T = if (!exists()) defaultValue else wrapped.get()!!

}