/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.permission

import com.ivianuu.essentials.permission.accessibility.accessibilityPermission
import com.ivianuu.essentials.permission.deviceadmin.deviceAdminPermission
import com.ivianuu.essentials.permission.dialogui.dialogPermission
import com.ivianuu.essentials.permission.ignorebatteryoptimizations.ignoreBatteryOptimizationsPermission
import com.ivianuu.essentials.permission.installunknownapps.installUnknownAppsPermission
import com.ivianuu.essentials.permission.intent.intentPermission
import com.ivianuu.essentials.permission.notificationlistener.notificationListenerPermission
import com.ivianuu.essentials.permission.packageusagestats.packageUsageStatsPermission
import com.ivianuu.essentials.permission.root.rootPermission
import com.ivianuu.essentials.permission.runtime.runtimePermission
import com.ivianuu.essentials.permission.systemoverlay.systemOverlayPermission
import com.ivianuu.essentials.permission.writesecuresettings.writeSecureSettingsPermission
import com.ivianuu.essentials.permission.writesettings.writeSettingsPermission
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.common.set

fun ComponentBuilder.esPermissions() {
    set<PermissionRequestHandler>(setQualifier = PermissionRequestHandlersSet)
    set<PermissionStateProvider>(setQualifier = PermissionStateProvidersSet)

    accessibilityPermission()
    deviceAdminPermission()
    dialogPermission()
    ignoreBatteryOptimizationsPermission()
    installUnknownAppsPermission()
    intentPermission()
    notificationListenerPermission()
    packageUsageStatsPermission()
    rootPermission()
    runtimePermission()
    systemOverlayPermission()
    writeSecureSettingsPermission()
    writeSettingsPermission()
}
