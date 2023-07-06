/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.widget.Toast
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.Strings
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

interface Toaster {
  operator fun invoke(message: String)

  operator fun invoke(messageKey: Strings.Key0)
}

@Provide class ToasterImpl(
  private val appContext: AppContext,
  private val coroutineContexts: CoroutineContexts,
  private val scope: ScopedCoroutineScope<AppScope>,
  private val strings: Strings
) : Toaster {
  override fun invoke(message: String) {
    scope.launch(coroutineContexts.main) {
      Toast.makeText(
        appContext,
        message,
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  override fun invoke(messageKey: Strings.Key0) =
    invoke(strings[messageKey])
}
