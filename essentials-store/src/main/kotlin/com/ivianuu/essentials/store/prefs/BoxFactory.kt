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

package com.ivianuu.essentials.store.prefs

import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.DiskBox
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class PrefBoxFactory(
    private val dispatcher: CoroutineDispatcher,
    private val prefsPath: String
) {

    private val boxes = ConcurrentHashMap<String, Box<*>>()

    private var cacheEnabled = AtomicBoolean(true)

    fun <T> box(
        name: String,
        defaultValue: T,
        serializer: DiskBox.Serializer<T>
    ): Box<T> {
        var box = if (cacheEnabled.get()) boxes[name] else null
        if (box?.isDisposed == true) box = null
        if (box == null) {
            box = DiskBox(
                path = "$prefsPath/$name",
                serializer = serializer,
                defaultValue = defaultValue,
                dispatcher = dispatcher
            )
            if (cacheEnabled.get()) boxes[name] = box
        }

        return box as Box<T>
    }

    internal suspend fun withoutCache(block: suspend () -> Unit) {
        cacheEnabled.set(false)
        block()
        cacheEnabled.set(true)
    }

}
