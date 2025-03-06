/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.content.*
import android.content.pm.*
import essentials.*
import essentials.coroutines.CoroutineContexts
import essentials.ui.UiScope
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

@Provide fun androidAppUiStarter(
  appContext: AppContext,
  appConfig: AppConfig,
  appScope: Scope<AppScope>,
  coroutineContexts: CoroutineContexts,
  packageManager: PackageManager,
) = AppUiStarter {
  withContext(coroutineContexts.main) {
    val intent = packageManager.getLaunchIntentForPackage(appConfig.packageName)!!
    appContext.startActivity(
      intent.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    )

    appScope.scopeOf<UiScope>().first().service()
  }
}
