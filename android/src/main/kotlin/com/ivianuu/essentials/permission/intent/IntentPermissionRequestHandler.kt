/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.intent

import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import splitties.coroutines.*

fun interface PermissionIntentFactory<P : Permission> : (P) -> Intent

@JvmInline value class ShowFindPermissionHint<P : Permission>(val value: Boolean)

@Provide fun <P : Permission> intentPermissionRequestHandler(
  appConfig: AppConfig,
  key: TypeKey<P>,
  intentFactory: PermissionIntentFactory<P>,
  navigator: Navigator,
  permissionManager: PermissionManager,
  showFindPermissionHint: ShowFindPermissionHint<P> = ShowFindPermissionHint(false),
  toaster: Toaster
) = PermissionRequestHandler<P> { permission ->
  raceOf(
    {
      if (showFindPermissionHint.value)
        toaster(R.string.find_app_here, appConfig.appName)
      // wait until user navigates back from the permission screen
      catch { navigator.push(DefaultIntentScreen(intentFactory(permission))) }
        .printErrors()
        .onLeft { toaster(R.string.grant_permission_manually) }
    },
    {
      // wait until user granted permission
      while (!permissionManager.permissionState(listOf(key)).first())
        delay(100)
    }
  )
}
