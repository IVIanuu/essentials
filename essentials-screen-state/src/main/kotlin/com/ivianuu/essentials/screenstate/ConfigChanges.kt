/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

object ConfigChange

@Provide fun configChanges(
  context: AppContext,
  coroutineContext: MainContext,
): Flow<ConfigChange> = callbackFlow<ConfigChange> {
  val callbacks = object : ComponentCallbacks2 {
    override fun onConfigurationChanged(newConfig: Configuration) {
      trySend(ConfigChange)
    }

    override fun onLowMemory() {
    }

    override fun onTrimMemory(level: Int) {
    }
  }
  context.registerComponentCallbacks(callbacks)
  awaitClose { context.unregisterComponentCallbacks(callbacks) }
}.flowOn(coroutineContext)
