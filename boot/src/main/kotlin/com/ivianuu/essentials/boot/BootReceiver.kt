/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.boot

import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*

fun interface BootListener {
  operator fun invoke()

  @Provide companion object {
    @Provide val defaultListeners get() = emptyList<BootListener>()
  }
}


@Provide @AndroidComponent class BootReceiver(
  private val bootListeners: () -> List<BootListener>,
  private val logger: Logger
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
    logger.log { "on system boot" }
    bootListeners().forEach { it() }
  }
}
