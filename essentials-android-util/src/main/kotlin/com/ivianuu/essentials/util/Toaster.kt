/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.Toast
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ResourceLoader
import com.ivianuu.essentials.ResourceLoaderWithArgs
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.inject
import kotlinx.coroutines.launch

fun interface Toaster {
  fun showToast(message: String)
}

context(AppContext, MainContext, NamedCoroutineScope<AppScope>)
    @Provide fun toaster() = Toaster { message ->
  launch(this@MainContext) {
    Toast.makeText(
      inject(),
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}

context(ResourceProvider, Toaster) fun showToast(messageRes: Int) {
  showToast(loadResource(messageRes))
}

context(ResourceProvider, Toaster) fun showToast(messageRes: Int, vararg args: Any?) {
  showToast(loadResourceWithArgs(messageRes, *args))
}

@Provide data class ToastContext(
  val resourceProvider: ResourceProvider,
  val toaster: Toaster
) : ResourceProvider by resourceProvider, Toaster by toaster {
  //  todo remove explicit delegates once fixed
  context(ResourceLoader<T>) override fun <T> loadResource(id: Int): T =
    resourceProvider.loadResource(id)

  context(ResourceLoaderWithArgs<T>) override fun <T> loadResourceWithArgs(
    id: Int,
    vararg args: Any?
  ): T = resourceProvider.loadResourceWithArgs(id, *args)
}
