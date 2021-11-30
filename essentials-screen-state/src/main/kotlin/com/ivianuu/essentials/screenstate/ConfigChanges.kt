/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.screenstate

import android.content.*
import android.content.res.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*

object ConfigChange

@Provide fun configChanges(
  context: AppContext,
  mainDispatcher: MainDispatcher,
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
}.flowOn(mainDispatcher)
