/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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

fun interface ContentChangesFactory : (Uri) -> Flow<Unit>

@Provide fun contentChangesFactory(
  contentResolver: ContentResolver,
  coroutineContext: MainContext,
) = ContentChangesFactory { uri ->
  callbackFlow<Unit> {
    val observer = withContext(coroutineContext) {
      object : ContentObserver(android.os.Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
          super.onChange(selfChange)
          trySend(Unit)
        }
      }
    }
    contentResolver.registerContentObserver(uri, false, observer)
    awaitClose { contentResolver.unregisterContentObserver(observer) }
  }.flowOn(coroutineContext)
}

@Provide inline fun contentResolver(application: Application): ContentResolver =
  application.contentResolver
