/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Looper
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

fun interface ContentChangesFactory {
  operator fun invoke(uri: Uri): Flow<Unit>
}

@Provide fun contentChangesFactory(
  contentResolver: ContentResolver,
  coroutineContexts: CoroutineContexts,
) = ContentChangesFactory { uri ->
  callbackFlow<Unit> {
    val observer = object : ContentObserver(android.os.Handler(Looper.getMainLooper())) {
      override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        trySend(Unit)
      }
    }
    contentResolver.registerContentObserver(uri, false, observer)
    awaitClose { contentResolver.unregisterContentObserver(observer) }
  }.flowOn(coroutineContexts.main)
}

@Provide inline fun contentResolver(application: Application): ContentResolver =
  application.contentResolver
