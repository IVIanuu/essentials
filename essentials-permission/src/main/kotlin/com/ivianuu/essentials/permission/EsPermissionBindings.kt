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

import com.ivianuu.essentials.permission.accessibility.AccessibilityServicePermissionStateProvider
import com.ivianuu.essentials.permission.deviceadmin.DeviceAdminPermissionStateProvider
import com.ivianuu.essentials.permission.dialogui.DialogPermissionRequestUi
import com.ivianuu.essentials.permission.ignorebatteryoptimizations.IgnoreBatteryOptimizationsPermissionStateProvider
import com.ivianuu.essentials.permission.installunknownapps.InstallUnknownAppsPermissionStateProvider
import com.ivianuu.essentials.permission.intent.IntentPermissionRequestHandler
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermissionStateProvider
import com.ivianuu.essentials.permission.packageusagestats.PackageUsageStatsPermissionStateProvider
import com.ivianuu.essentials.permission.root.RootPermissionRequestHandler
import com.ivianuu.essentials.permission.root.RootPermissionStateProvider
import com.ivianuu.essentials.permission.runtime.RuntimePermissionRequestHandler
import com.ivianuu.essentials.permission.runtime.RuntimePermissionStateProvider
import com.ivianuu.essentials.permission.systemoverlay.SystemOverlayPermissionStateProvider
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermissionRequestHandler
import com.ivianuu.essentials.permission.writesecuresettings.WriteSecureSettingsPermissionStateProvider
import com.ivianuu.essentials.permission.writesettings.WriteSettingsPermissionStateProvider
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.common.set

fun ComponentBuilder.esPermissionBindings() {
    set<PermissionRequestHandler>(setQualifier = PermissionRequestHandlersSet)
    set<PermissionStateProvider>(setQualifier = PermissionStateProvidersSet)

    bindPermissionStateProviderIntoSet<AccessibilityServicePermissionStateProvider>()

    bindPermissionStateProviderIntoSet<DeviceAdminPermissionStateProvider>()

    permissionRequestUi<DialogPermissionRequestUi>()

    bindPermissionStateProviderIntoSet<IgnoreBatteryOptimizationsPermissionStateProvider>()

    bindPermissionStateProviderIntoSet<InstallUnknownAppsPermissionStateProvider>()

    bindPermissionRequestHandlerIntoSet<IntentPermissionRequestHandler>()

    bindPermissionStateProviderIntoSet<NotificationListenerPermissionStateProvider>()

    bindPermissionStateProviderIntoSet<PackageUsageStatsPermissionStateProvider>()

    bindPermissionRequestHandlerIntoSet<RootPermissionRequestHandler>()
    bindPermissionStateProviderIntoSet<RootPermissionStateProvider>()

    bindPermissionRequestHandlerIntoSet<RuntimePermissionRequestHandler>()
    bindPermissionStateProviderIntoSet<RuntimePermissionStateProvider>()

    bindPermissionStateProviderIntoSet<SystemOverlayPermissionStateProvider>()

    bindPermissionRequestHandlerIntoSet<WriteSecureSettingsPermissionRequestHandler>()
    bindPermissionStateProviderIntoSet<WriteSecureSettingsPermissionStateProvider>()

    bindPermissionStateProviderIntoSet<WriteSettingsPermissionStateProvider>()
}
