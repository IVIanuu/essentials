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

typealias PermissionIntentFactory<P> = (P) -> Intent

typealias ShowFindPermissionHint<P> = Boolean

@Provide fun <P : Permission> intentPermissionRequestHandler(
  buildInfo: BuildInfo,
  intentFactory: PermissionIntentFactory<P>,
  navigator: Navigator,
  showFindPermissionHint: ShowFindPermissionHint<P> = false,
  state: Flow<PermissionState<P>>,
  rp: ResourceProvider,
  toaster: Toaster
): PermissionRequestHandler<P> = { permission ->
  race(
    {
      if (showFindPermissionHint)
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
