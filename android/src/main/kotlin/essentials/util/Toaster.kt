/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.app.*
import android.widget.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

@Tag typealias showToastResult = Unit
typealias showToast = suspend (String) -> showToastResult

@Provide suspend fun showToast(
  message: String,
  context: Application,
  coroutineContexts: CoroutineContexts
): showToastResult {
  withContext(coroutineContexts.main) {
    Toast.makeText(
      context,
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}
