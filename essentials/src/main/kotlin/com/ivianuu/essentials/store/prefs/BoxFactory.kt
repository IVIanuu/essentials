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

import android.content.Context
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.DiskBox
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@ApplicationScope
@Single
class PrefBoxFactory(
    @ApplicationScope private val context: Context,
    private val dispatchers: AppDispatchers,
    @PrefsDir private val prefsDir: File
) {

    private val boxes = ConcurrentHashMap<String, Box<*>>()

    fun <T> box(
        name: String,
        defaultValue: T,
        serializer: DiskBox.Serializer<T>
    ): Box<T> {
        var box = boxes[name]
        if (box?.isDisposed == true) box = null
        if (box == null) {
            box = DiskBox(
                context = context,
                path = "${prefsDir.absolutePath}/$name",
                serializer = serializer,
                defaultValue = defaultValue,
                dispatcher = dispatchers.io
            )
            boxes[name] = box
        }

        return box as Box<T>
    }
}