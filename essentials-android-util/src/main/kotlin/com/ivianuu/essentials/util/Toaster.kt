/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.Toast
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Resources
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.MainCoroutineContext
import com.ivianuu.injekt.common.NamedCoroutineScope
import kotlinx.coroutines.launch

fun interface Toaster {
  operator fun invoke(message: String)
}

@Provide fun toaster(
  appContext: AppContext,
  mainCoroutineContext: MainCoroutineContext,
  scope: NamedCoroutineScope<AppScope>
) = Toaster { message ->
  scope.launch(mainCoroutineContext) {
    Toast.makeText(
      appContext,
      message,
      Toast.LENGTH_SHORT
    ).show()
  }
}

operator fun Toaster.invoke(messageRes: Int, @Inject resources: Resources) {
  this(resources<String>(messageRes))
}

operator fun Toaster.invoke(
  messageRes: Int,
  vararg args: Any?,
  @Inject resources: Resources
) {
  this(resources<String>(messageRes, *args))
}

