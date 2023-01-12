/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.processrestart

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide

fun interface ProcessRestarter {
  suspend fun restartProcess()
}

context(AppContext, BuildInfo, Logger) @Provide fun processRestarter() = ProcessRestarter {
  val intent = packageManager.getLaunchIntentForPackage(packageName)!!
    .addFlags(FLAG_ACTIVITY_NEW_TASK)
  log { "restart process $intent" }
  ProcessRestartActivity.launch(intent)
  Runtime.getRuntime().exit(0)
}
