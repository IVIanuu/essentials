/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.Toast
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.MainContext
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch

fun interface Toaster {
  operator fun invoke(message: String)
}

@Provide fun toaster(
  appContext: AppContext,
  mainContext: MainContext,
  scope: NamedCoroutineScope<AppScope>
) = Toaster { message ->
  scope.launch(mainContext) {
    Toast.makeText(
      appContext,
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}

operator fun Toaster.invoke(messageRes: Int, @Inject resourceProvider: ResourceProvider) {
  this(resourceProvider<String>(messageRes))
}

operator fun Toaster.invoke(
  messageRes: Int,
  vararg args: Any?,
  @Inject resourceProvider: ResourceProvider
) {
  this(resourceProvider<String>(messageRes, *args))
}

