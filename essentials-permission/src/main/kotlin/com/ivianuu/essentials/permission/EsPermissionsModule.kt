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
import com.ivianuu.essentials.permission.dialogui.DialogPermissionRequestUi
import com.ivianuu.essentials.permission.intent.IntentPermissionRequestHandler
import com.ivianuu.essentials.permission.runtime.RuntimePermissionRequestHandler
import com.ivianuu.essentials.permission.runtime.RuntimePermissionStateProvider
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.module

val esPermissionsModule = module {
    single(name = PermissionNavigator) { Navigator() }

    set<PermissionRequestHandler>(setName = PermissionRequestHandlers)
    set<PermissionStateProvider>(setName = PermissionStateProviders)

    // accessibility
    bindPermissionStateProvider<AccessibilityServicePermissionStateProvider>()

    // dialog ui
    bindPermissionRequestUi<DialogPermissionRequestUi>()

    // intent
    bindPermissionRequestHandler<IntentPermissionRequestHandler>()

    // runtime
    bindPermissionRequestHandler<RuntimePermissionRequestHandler>()
    bindPermissionStateProvider<RuntimePermissionStateProvider>()
}