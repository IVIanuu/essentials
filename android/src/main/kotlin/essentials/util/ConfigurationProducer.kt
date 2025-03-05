/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.content.*
import android.content.res.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*

@Provide class ConfigurationProducer(
  private val appContext: AppContext
) {
  @Composable fun configuration(): Configuration =
    produceState(appContext.resources.configuration) {
      val callbacks = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
          value = newConfig
        }

        override fun onLowMemory() {
        }

        override fun onTrimMemory(level: Int) {
        }
      }
      appContext.registerComponentCallbacks(callbacks)
      awaitDispose { appContext.unregisterComponentCallbacks(callbacks) }
    }.value
}
