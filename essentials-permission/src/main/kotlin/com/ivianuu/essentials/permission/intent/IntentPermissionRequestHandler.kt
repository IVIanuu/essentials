/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.intent

import android.content.Intent
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.race
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.toIntentKey
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

fun interface PermissionIntentFactory<P : Permission> : (P) -> Intent

@JvmInline value class ShowFindPermissionHint<P : Permission>(val value: Boolean)

@Provide fun <P : Permission> intentPermissionRequestHandler(
  buildInfo: BuildInfo,
  intentFactory: PermissionIntentFactory<P>,
  navigator: Navigator,
  showFindPermissionHint: ShowFindPermissionHint<P> = ShowFindPermissionHint(false),
  state: Flow<PermissionState<P>>,
  T: ToastContext
) = PermissionRequestHandler<P> { permission ->
  race(
    {
      if (showFindPermissionHint.value)
        showToast(R.string.es_find_app_here, buildInfo.appName)
      // wait until user navigates back from the permission screen
      catch { navigator.push(intentFactory(permission).toIntentKey()) }
        .onFailure { showToast(R.string.es_grant_permission_manually) }
    },
    {
      // wait until user granted permission
      while (!state.first()) delay(100)
    }
  )
}
