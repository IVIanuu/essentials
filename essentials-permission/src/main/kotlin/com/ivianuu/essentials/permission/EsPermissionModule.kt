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

import com.ivianuu.essentials.permission.accessibility.EsAccessibilityServicePermissionModule
import com.ivianuu.essentials.permission.deviceadmin.EsDeviceAdminPermissionModule
import com.ivianuu.essentials.permission.dialogui.EsDialogPermissionUiModule
import com.ivianuu.essentials.permission.ignorebatteryoptimizations.EsIgnoreBatteryOptimizationsPermissionModule
import com.ivianuu.essentials.permission.installunknownapps.EsInstallUnknownAppsPermissionModule
import com.ivianuu.essentials.permission.intent.EsIntentPermissionRequestHandlerModule
import com.ivianuu.essentials.permission.notificationlistener.EsNotificationListenerPermissionModule
import com.ivianuu.essentials.permission.packageusagestats.EsPackageUsageStatsPermission
import com.ivianuu.essentials.permission.root.EsRootPermissionModule
import com.ivianuu.essentials.permission.runtime.EsRuntimePermissionModule
import com.ivianuu.essentials.permission.systemoverlay.EsSystemOverlayPermissionModule
import com.ivianuu.essentials.permission.writesettings.EsWriteSettingsPermissionModule
import com.ivianuu.injekt.Module

val EsPermissionModule = Module {
    set<PermissionRequestHandler>(setName = PermissionRequestHandlers)
    set<PermissionStateProvider>(setName = PermissionStateProviders)

    include(EsAccessibilityServicePermissionModule)
    include(EsDeviceAdminPermissionModule)
    include(EsDialogPermissionUiModule)
    include(EsIgnoreBatteryOptimizationsPermissionModule)
    include(EsInstallUnknownAppsPermissionModule)
    include(EsIntentPermissionRequestHandlerModule)
    include(EsNotificationListenerPermissionModule)
    include(EsPackageUsageStatsPermission)
    include(EsRootPermissionModule)
    include(EsRuntimePermissionModule)
    include(EsSystemOverlayPermissionModule)
    include(EsWriteSettingsPermissionModule)
}
