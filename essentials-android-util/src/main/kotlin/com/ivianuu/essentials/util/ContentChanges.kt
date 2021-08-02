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

package com.ivianuu.essentials.util

import android.app.*
import android.content.*
import android.database.*
import android.net.*
import android.os.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

typealias ContentChangesFactory = (Uri) -> Flow<Unit>

@Provide fun contentChangesFactory(
  contentResolver: ContentResolver,
  mainDispatcher: MainDispatcher,
): ContentChangesFactory = { uri ->
  callbackFlow<Unit> {
    val observer = withContext(mainDispatcher) {
      object : ContentObserver(android.os.Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
          super.onChange(selfChange)
          trySend(Unit)
        }
      }
    }
    contentResolver.registerContentObserver(uri, false, observer)
    awaitClose { contentResolver.unregisterContentObserver(observer) }
  }.flowOn(mainDispatcher)
}

@Provide inline val Application.bindContentResolver: ContentResolver
  get() = contentResolver
