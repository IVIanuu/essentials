/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.processrestart

import android.content.Intent.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*

fun interface ProcessRestarter : suspend () -> Unit

@Provide fun processRestarter(
  buildInfo: BuildInfo,
  context: AppContext,
  L: Logger,
  packageManager: PackageManager,
) = ProcessRestarter {
  val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
    .addFlags(FLAG_ACTIVITY_NEW_TASK)
  log { "restart process $intent" }
  ProcessRestartActivity.launch(context, intent)
  Runtime.getRuntime().exit(0)
}
