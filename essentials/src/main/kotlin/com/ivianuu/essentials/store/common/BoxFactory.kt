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

package com.ivianuu.essentials.store.common

import android.content.Context
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.store.DiskBox
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.injekt.Single
import java.io.File

@Single
class PrefBoxFactory(
    private val context: Context,
    private val dispatchers: AppDispatchers,
    @PrefsDir private val prefsDir: File
) {

    fun <T> box(
        name: String,
        defaultValue: suspend () -> T,
        serializer: DiskBox.Serializer<T>
    ): Box<T> {
        return DiskBox(
            context = context,
            file = File(prefsDir, name),
            defaultValue = defaultValue,
            serializer = serializer,
            dispatcher = dispatchers.io
        )
    }

}