/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
