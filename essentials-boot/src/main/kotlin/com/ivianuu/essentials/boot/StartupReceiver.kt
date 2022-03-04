/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.boot

import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

class StartupReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
    val component = context.applicationContext
      .let { it as AppElementsOwner }
      .appElements<StartupReceiverComponent>()

    log(logger = component.logger) { "on system boot" }
    component.bootListeners.forEach { it() }
  }
}

@Provide @Element<AppScope>
data class StartupReceiverComponent(val bootListeners: List<BootListener>, val logger: Logger)
