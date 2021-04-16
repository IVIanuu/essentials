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

package com.ivianuu.essentials.util

import android.app.*
import android.content.*
import android.database.*
import android.net.*
import android.os.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

typealias ContentChangesFactory = (Uri) -> Flow<Unit>

@Given
fun contentChangesFactory(
    @Given contentResolver: ContentResolver,
    @Given mainDispatcher: MainDispatcher,
): ContentChangesFactory = { uri ->
    callbackFlow<Unit> {
        val observer = withContext(mainDispatcher) {
            object : ContentObserver(android.os.Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    runCatching { offer(Unit) }
                }
            }
        }
        contentResolver.registerContentObserver(uri, false, observer)
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }.flowOn(mainDispatcher)
}

@Given
inline val Application.bindContentResolver: ContentResolver
    get() = contentResolver
