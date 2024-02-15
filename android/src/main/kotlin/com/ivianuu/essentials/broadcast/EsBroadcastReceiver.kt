/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import arrow.fx.coroutines.parMap
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide @AndroidComponent class EsBroadcastReceiver(
  private val handlers: List<BroadcastHandler>,
  private val scope: ScopedCoroutineScope<AppScope>
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    scope.launch {
      handlers.parMap { it(intent) }
    }
  }
}

fun interface BroadcastHandler {
  suspend operator fun invoke(intent: Intent)

  @Provide companion object {
    inline operator fun invoke(
      action: String,
      crossinline handle: suspend (Intent) -> Unit
    ) = BroadcastHandler {
      if (it.action == action)
        handle(it)
    }

    @Provide val defaultHandlers get() = emptyList<BroadcastHandler>()
  }
}
