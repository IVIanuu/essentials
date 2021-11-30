/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.processrestart

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag

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
