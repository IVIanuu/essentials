/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.permission.intent

import android.content.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
        .onFailure {
          showToast(R.string.es_grant_permission_manually)
        }
    },
    {
      // wait until user granted permission
      while (!state.first()) delay(100)
    }
  )
}
