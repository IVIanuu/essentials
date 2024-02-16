/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.intent

import android.content.Intent
import arrow.core.Either
import arrow.fx.coroutines.raceN
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.ui.navigation.DefaultIntentScreen
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

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
  raceN(
    {
      if (showFindPermissionHint.value)
        toaster(R.string.find_app_here, appConfig.appName)
      // wait until user navigates back from the permission screen
      Either.catch { navigator.push(DefaultIntentScreen(intentFactory(permission))) }
        .onLeft { toaster(R.string.grant_permission_manually) }
    },
    {
      // wait until user granted permission
      while (!permissionManager.permissionState(listOf(key)).first())
        delay(100)
    }
  )
}
