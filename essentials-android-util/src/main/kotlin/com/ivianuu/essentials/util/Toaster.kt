/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*

fun interface Toaster : (String) -> Unit

@Provide fun toaster(
  context: AppContext,
  coroutineContext: MainContext,
  scope: NamedCoroutineScope<AppScope>
) = Toaster { message ->
  scope.launch(coroutineContext) {
    Toast.makeText(
      context,
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}

@Provide data class ToastContext(
  @Provide val toaster: Toaster,
  @Provide val resourceProvider: ResourceProvider
)

fun showToast(message: String, @Inject toaster: Toaster) {
  toaster(message)
}

fun showToast(messageRes: Int, @Inject T: Toaster, @Inject RP: ResourceProvider) {
  showToast(message = loadResource(messageRes))
}

fun showToast(messageRes: Int, vararg args: Any?, @Inject T: Toaster, @Inject RP: ResourceProvider) {
  showToast(message = loadResource(messageRes, *args))
}
