/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.content.*
import essentials.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import splitties.coroutines.*
import kotlin.reflect.*

fun interface PermissionIntentFactory<P : Permission> {
  fun createIntent(permission: P): Intent
}

@JvmInline value class ShowFindPermissionHint<P : Permission>(val value: Boolean)

@Provide fun <P : Permission> intentPermissionRequestHandler(
  appConfig: AppConfig,
  key: KClass<P>,
  intentFactory: PermissionIntentFactory<P>,
  navigator: Navigator,
  permissionManager: PermissionManager,
  showFindPermissionHint: ShowFindPermissionHint<P> = ShowFindPermissionHint(false),
  toaster: Toaster
) = PermissionRequestHandler<P> { permission ->
  raceOf(
    {
      if (showFindPermissionHint.value)
        toaster.toast("Find ${appConfig.appName} here")
      // wait until user navigates back from the permission screen
      catch { navigator.push(intentFactory.createIntent(permission).asScreen()) }
        .printErrors()
        .onLeft { toaster.toast("Couldn\'t open settings screen! Please grant the permission manually") }
    },
    {
      // wait until user granted permission
      // we intentionally call it again and again to force a refresh
      while (!permissionManager.permissionState(listOf(key)).first())
        delay(100)
    }
  )
}
