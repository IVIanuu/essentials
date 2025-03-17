/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.app.*
import android.content.*
import essentials.*
import essentials.app.*
import essentials.coroutines.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide suspend fun launchAndroidUi(
  context: Application,
  appConfig: AppConfig,
  appScope: Scope<AppScope>,
  coroutineContexts: CoroutineContexts,
): launchUiResult = withContext(coroutineContexts.main) {
  val intent = context.packageManager.getLaunchIntentForPackage(appConfig.packageName)!!
  context.startActivity(
    intent.apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )

  appScope.scopeOf<UiScope>().first()
}
