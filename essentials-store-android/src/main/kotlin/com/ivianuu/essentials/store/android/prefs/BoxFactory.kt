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

package com.ivianuu.essentials.store.android.prefs

import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.DiskBox
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap

class PrefBoxFactory(
    private val coroutineScope: CoroutineScope,
    @PublishedApi internal val moshi: Moshi,
    private val prefsPath: String
) {

    private val boxes = ConcurrentHashMap<String, Box<*>>()

    inline fun <reified T> create(
        name: String,
        defaultValue: T
    ): Box<T> = create(
        name = name,
        defaultValue = defaultValue,
        serializer = MoshiSerializer(moshi.adapter(javaTypeOf<T>()))
    )

    fun <T> create(
        name: String,
        defaultValue: T,
        serializer: DiskBox.Serializer<T>
    ): Box<T> {
        var box = boxes[name]
        if (box == null) {
            box = DiskBox(
                path = "$prefsPath/$name",
                serializer = serializer,
                defaultValue = defaultValue,
                coroutineScope = coroutineScope
            )
            boxes[name] = box
        }

        return box as Box<T>
    }
}
