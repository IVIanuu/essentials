/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import android.database.*
import android.net.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flowOn

@Stable @Provide class ContentChangeManager(
  private val contentResolver: ContentResolver,
  private val coroutineContexts: CoroutineContexts,
) {
  fun contentChanges(uri: Uri): Flow<Unit> = callbackFlow {
    val observer = object : ContentObserver(null) {
      override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        trySend(Unit)
      }
    }
    contentResolver.registerContentObserver(uri, false, observer)
    awaitClose { contentResolver.unregisterContentObserver(observer) }
  }.flowOn(coroutineContexts.main)
}
