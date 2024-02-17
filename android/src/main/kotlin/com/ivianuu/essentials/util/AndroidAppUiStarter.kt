/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ScopeManager
import com.ivianuu.essentials.app.AppVisibleScope
import com.ivianuu.essentials.scopeOf
import com.ivianuu.essentials.ui.navigation.AppUiStarter
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first
@Provide fun androidAppUiStarter(
  appContext: AppContext,
  appConfig: AppConfig,
  packageManager: PackageManager,
  scopeManager: ScopeManager,
) = AppUiStarter {
  val intent = packageManager.getLaunchIntentForPackage(appConfig.packageName)!!
  appContext.startActivity(
    intent.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )

  scopeManager.scopeOf<AppVisibleScope>().first().service()
}
