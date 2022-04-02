/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.parForEach
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.launch

class EsBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val component = context.applicationContext
      .let { it as AppElementsOwner }
      .appElements<EsBroadcastReceiverComponent>()
    component.scope.launch {
      component.handlers.parForEach { it(intent) }
    }
  }
}

@Provide @Element<AppScope>
data class EsBroadcastReceiverComponent(
  val handlers: List<BroadcastHandler>,
  val scope: NamedCoroutineScope<AppScope>
)

fun interface BroadcastHandler : suspend (Intent) -> Unit {
  companion object {
    inline operator fun invoke(
      action: String,
      crossinline handle: suspend (Intent) -> Unit
    ) = BroadcastHandler {
      if (it.action == action)
        handle(it)
    }

    @Provide val defaultHandlers: List<BroadcastHandler>
      get() = emptyList()
  }
}
