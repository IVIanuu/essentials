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

package com.ivianuu.essentials.permission.systemoverlay

import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.BuildInfo
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.intent.PermissionIntentFactory
import com.ivianuu.essentials.permission.intent.ShowFindPermissionHint
import com.ivianuu.injekt.Provide

interface SystemOverlayPermission : Permission

@Provide fun <P : SystemOverlayPermission> systemOverlayPermissionStateProvider(
  context: AppContext
) = PermissionStateProvider<P> { Settings.canDrawOverlays(context) }

@Provide fun <P : SystemOverlayPermission> systemOverlayShowFindPermissionHint(
  systemBuildInfo: SystemBuildInfo
) = ShowFindPermissionHint<P>(systemBuildInfo.sdk >= 30)

@Provide fun <P : SystemOverlayPermission> systemOverlayPermissionIntentFactory(
  buildInfo: BuildInfo
) = PermissionIntentFactory<P> {
  Intent(
    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    "package:${buildInfo.packageName}".toUri()
  )
}
