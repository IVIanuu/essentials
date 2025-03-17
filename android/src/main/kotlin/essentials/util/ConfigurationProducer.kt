/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.app.*
import android.content.*
import android.content.res.*
import androidx.compose.runtime.*
import injekt.*

@Provide @Composable fun configuration(context: Application): Configuration =
  produceState(context.resources.configuration) {
    val callbacks = object : ComponentCallbacks2 {
      override fun onConfigurationChanged(newConfig: Configuration) {
        value = newConfig
      }

      override fun onLowMemory() {
      }

      override fun onTrimMemory(level: Int) {
      }
    }
    context.registerComponentCallbacks(callbacks)
    awaitDispose { context.unregisterComponentCallbacks(callbacks) }
  }.value
