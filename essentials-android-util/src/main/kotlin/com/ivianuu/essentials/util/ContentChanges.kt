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

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Looper
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

fun interface ContentChangesFactory : (Uri) -> Flow<Unit>

@Provide fun contentChangesFactory(
  contentResolver: ContentResolver,
  mainDispatcher: MainDispatcher,
) = ContentChangesFactory { uri ->
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

@Provide inline fun contentResolver(application: Application): ContentResolver =
  application.contentResolver
