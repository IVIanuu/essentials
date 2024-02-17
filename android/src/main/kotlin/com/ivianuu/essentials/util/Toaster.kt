/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Provide class Toaster(
  private val appContext: AppContext,
  private val coroutineContexts: CoroutineContexts,
  private val resources: Resources,
  private val scope: ScopedCoroutineScope<AppScope>
) {
  operator fun invoke(message: String) {
    scope.launch(coroutineContexts.main) {
      Toast.makeText(
        appContext,
        message,
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  operator fun invoke(messageRes: Int) = invoke(resources<String>(messageRes))

  operator fun invoke(messageRes: Int, vararg args: Any?) =
    invoke(resources<String>(messageRes, *args))
}
