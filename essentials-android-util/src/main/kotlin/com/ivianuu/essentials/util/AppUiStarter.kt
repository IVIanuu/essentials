/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.state.asComposedFlow
import com.ivianuu.essentials.ui.navigation.IntentAppUiStarter
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

fun interface AppUiStarter : IntentAppUiStarter

@Provide fun intentAppUiStarter(appUiStarter: AppUiStarter): IntentAppUiStarter = appUiStarter

@Provide fun appUiStarter(
  context: AppContext,
  buildInfo: BuildInfo,
  foregroundActivity: @Composable () -> ForegroundActivity,
  packageManager: PackageManager,
) = AppUiStarter {
  val intent = packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
  context.startActivity(
    intent.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )

  foregroundActivity.asComposedFlow().filterNotNull().first()
}
