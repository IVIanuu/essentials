/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.boot

import android.content.*
import co.touchlab.kermit.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

fun interface BootListener {
  fun onBoot()

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
    logger.d { "on system boot" }
    bootListeners().forEach { it.onBoot() }
  }
}
