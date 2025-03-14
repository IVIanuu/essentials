/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.util

import android.content.*
import android.content.pm.*
import essentials.*
import essentials.coroutines.*
import essentials.ui.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

suspend fun launchUi(scope: Scope<*> = inject): Scope<UiScope> =
  withContext(coroutineContexts().main) {
    val intent = packageManager()
      .getLaunchIntentForPackage(appConfig().packageName)!!
    appContext().startActivity(
      intent.apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    )

    scope.scopeOf<UiScope>().first()
  }

@Provide fun androidUiLauncher(scope: Scope<*> = inject) = UiLauncher { launchUi() }
