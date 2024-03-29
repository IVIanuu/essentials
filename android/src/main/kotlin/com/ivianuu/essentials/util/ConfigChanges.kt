/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import android.content.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flowOn

object ConfigChange

@Provide fun configChanges(
  appContext: AppContext,
  coroutineContexts: CoroutineContexts,
): Flow<ConfigChange> = callbackFlow {
  val callbacks = object : ComponentCallbacks2 {
    override fun onConfigurationChanged(newConfig: Configuration) {
      trySend(ConfigChange)
    }

    override fun onLowMemory() {
    }

    override fun onTrimMemory(level: Int) {
    }
  }
  appContext.registerComponentCallbacks(callbacks)
  awaitClose { appContext.unregisterComponentCallbacks(callbacks) }
}.flowOn(coroutineContexts.main)
