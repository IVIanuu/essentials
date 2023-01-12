/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.ui.navigation.IntentAppUiStarter
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

fun interface AppUiStarter : IntentAppUiStarter

@Provide fun intentAppUiStarter(appUiStarter: AppUiStarter): IntentAppUiStarter = appUiStarter

context(AppContext, BuildInfo, ForegroundActivityProvider)
    @Provide fun appUiStarter() = AppUiStarter {
  val intent = packageManager.getLaunchIntentForPackage(packageName)!!
  startActivity(
    intent.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )

  foregroundActivity.filterNotNull().first()
}
