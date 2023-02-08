/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.processrestart

import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.invoke
import com.ivianuu.injekt.Provide

fun interface ProcessRestarter {
  operator fun invoke()
}

@Provide fun processRestarter(
  appContext: AppContext,
  buildInfo: BuildInfo,
  logger: Logger,
  packageManager: PackageManager,
) = ProcessRestarter {
  val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
    .addFlags(FLAG_ACTIVITY_NEW_TASK)
  logger { "restart process $intent" }
  ProcessRestartActivity.launch(appContext, intent)
  Runtime.getRuntime().exit(0)
}
