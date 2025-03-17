/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.permission

import android.content.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import splitties.coroutines.*
import kotlin.reflect.*

data class IntentPermissionRequestParams<P : Permission>(
  val intent: Intent,
  val showFindHint: Boolean = false
)

@Provide suspend fun <P : Permission> requestPermissionWithIntent(
  data: IntentPermissionRequestParams<P>,
  key: KClass<P>,
  appConfig: AppConfig,
  navigator: Navigator,
  permissions: Permissions,
  showToast: showToast
): PermissionRequestResult<P> = raceOf(
  {
    if (data.showFindHint)
      showToast("Find ${appConfig.appName} here")
    // wait until user navigates back from the permission screen
    catch { navigator.push(data.intent.asScreen()) }
      .printErrors()
      .onFailure { showToast("Couldn\'t open settings screen! Please grant the permission manually") }
  },
  {
    // wait until user granted permission
    // we intentionally call it again and again to force a refresh
    while (!permissions.permissionState(listOf(key)).first())
      delay(100)
  }
)
