/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.invoke
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Element

class StartupReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
    val component = context.applicationContext
      .cast<AppElementsOwner>()
      .appElements
      .element<StartupReceiverComponent>()

    component.logger { "on system boot" }
    component.bootListeners.forEach { it() }
  }
}

@Provide @Element<AppScope>
data class StartupReceiverComponent(val bootListeners: List<BootListener>, @Provide val logger: Logger)
