/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
