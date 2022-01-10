/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

fun interface AppUiStarter : IntentAppUiStarter

@Provide fun intentAppUiStarter(appUiStarter: AppUiStarter): IntentAppUiStarter = appUiStarter

@Provide fun appUiStarter(
  context: AppContext,
  buildInfo: BuildInfo,
  foregroundActivity: Flow<ForegroundActivity>,
  packageManager: PackageManager,
) = AppUiStarter {
  val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
  context.startActivity(
    intent.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )

  foregroundActivity.filterNotNull().first()
}
