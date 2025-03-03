/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

@Stable @Provide class Toaster(
  private val appContext: AppContext,
  private val coroutineContexts: CoroutineContexts,
  private val scope: ScopedCoroutineScope<AppScope>
) {
  fun toast(message: String) {
    scope.launch(coroutineContexts.main) {
      Toast.makeText(
        appContext,
        message,
        Toast.LENGTH_SHORT
      ).show()
    }
  }
}
