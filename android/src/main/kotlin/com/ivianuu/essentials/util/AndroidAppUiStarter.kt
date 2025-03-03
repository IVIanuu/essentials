/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.util

import android.content.*
import android.content.pm.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.*
import com.ivianuu.essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide fun androidAppUiStarter(
  appContext: AppContext,
  appConfig: AppConfig,
  appScope: Scope<AppScope>,
  packageManager: PackageManager,
) = AppUiStarter {
  val intent = packageManager.getLaunchIntentForPackage(appConfig.packageName)!!
  appContext.startActivity(
    intent.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )

  appScope.scopeOf<AppVisibleScope>().first().service()
}
