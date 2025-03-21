/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.app.*
import android.widget.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*

@Tag typealias showToastResult = Unit
typealias showToast = (String) -> showToastResult

@Provide fun showToast(
  message: String,
  context: Application,
  coroutineContexts: CoroutineContexts,
  scope: ScopedCoroutineScope<AppScope>
): showToastResult {
  scope.launch(coroutineContexts.main) {
    Toast.makeText(
      context,
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}
