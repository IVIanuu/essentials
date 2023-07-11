/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

fun interface BootListener {
  operator fun invoke()

  companion object {
    @Provide val defaultListeners get() = emptyList<BootListener>()
  }
}


@Provide @AndroidComponent class BootReceiver(
  private val bootListeners: () ->List<BootListener>,
  private val logger: Logger
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
    logger.log { "on system boot" }
    bootListeners().forEach { it() }
  }
}
