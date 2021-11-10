/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Tag annotation class PermissionIntentFactoryTag
typealias PermissionIntentFactory<P> = @PermissionIntentFactoryTag (P) -> Intent

@JvmInline value class ShowFindPermissionHint<P>(val value: Boolean)

@Provide fun <P : Permission> intentPermissionRequestHandler(
  buildInfo: BuildInfo,
  intentFactory: PermissionIntentFactory<P>,
  navigator: Navigator,
  showFindPermissionHint: ShowFindPermissionHint<P> = ShowFindPermissionHint(false),
  state: Flow<PermissionState<P>>,
  T: ToastContext
): PermissionRequestHandler<P> = { permission ->
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
