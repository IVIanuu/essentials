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

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.injekt.Given
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

typealias ContentChangesFactory = (Uri) -> Flow<Unit>

@Given
fun contentChangesFactory(
    @Given contentResolver: ContentResolver,
    @Given mainDispatcher: MainDispatcher,
): ContentChangesFactory = { uri ->
    callbackFlow<Unit> {
        val observer = withContext(mainDispatcher) {
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    offerSafe(Unit)
                }
            }
        }
        contentResolver.registerContentObserver(uri, false, observer)
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }.flowOn(mainDispatcher)
}

@Given
inline val @Given Application.bindContentResolver: ContentResolver
    get() = contentResolver
