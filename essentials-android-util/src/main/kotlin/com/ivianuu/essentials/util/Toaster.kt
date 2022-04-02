/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.Toast
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch

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
