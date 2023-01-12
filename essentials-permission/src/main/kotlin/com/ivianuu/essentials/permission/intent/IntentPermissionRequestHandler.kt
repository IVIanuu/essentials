/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.intent

import android.content.Intent
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.ui.navigation.DefaultIntentKey
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

fun interface PermissionIntentFactory<P : Permission> {
  suspend fun createPermissionIntent(permission: P): Intent
}

@JvmInline value class ShowFindPermissionHint<P : Permission>(val value: Boolean)

context(PermissionIntentFactory<P>, ToastContext) @Provide fun <P : Permission> intentPermissionRequestHandler(
  buildInfo: BuildInfo,
  navigator: Navigator,
  permissionKey: TypeKey<P>,
  showFindPermissionHint: ShowFindPermissionHint<P> = ShowFindPermissionHint(false),
  permissionManager: PermissionManager
) = PermissionRequestHandler<P> { permission ->
  race(
    {
      if (showFindPermissionHint.value)
        showToast(R.string.es_find_app_here, buildInfo.appName)
      // wait until user navigates back from the permission screen
      catch { navigator.push(DefaultIntentKey(createPermissionIntent(permission))) }
        .onFailure {
          showToast(R.string.es_grant_permission_manually)
        }
    },
    {
      // wait until user granted permission
      while (!permissionManager.permissionState(listOf(permissionKey)).first()) delay(100)
    }
  )
}
