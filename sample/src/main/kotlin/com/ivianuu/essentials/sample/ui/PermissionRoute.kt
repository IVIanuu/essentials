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

package com.ivianuu.essentials.sample.ui

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.permission.Desc
import com.ivianuu.essentials.permission.Icon
import com.ivianuu.essentials.permission.MetadataKeys
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.R
import com.ivianuu.essentials.permission.RuntimePermission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.ui.compose.common.SimpleScreen
import com.ivianuu.essentials.ui.compose.coroutines.launchOnActive
import com.ivianuu.essentials.ui.compose.es.composeControllerRoute
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.resources.drawableResource
import com.ivianuu.essentials.ui.navigation.director.controllerRouteOptions
import com.ivianuu.essentials.ui.navigation.director.horizontal

val permissionRoute = composeControllerRoute(
    options = controllerRouteOptions().horizontal()
) {
    SimpleScreen(title = "Permissions") {
        val manager = inject<PermissionManager>()

        val camera = RuntimePermission(
            android.Manifest.permission.CAMERA,
            MetadataKeys.Title to "Camera",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        val phone = RuntimePermission(
            android.Manifest.permission.CALL_PHONE,
            MetadataKeys.Title to "Call phone",
            MetadataKeys.Desc to "This is a desc",
            MetadataKeys.Icon to drawableResource(R.drawable.es_ic_menu)
        )

        launchOnActive {
            val granted = manager.request(camera, phone)
            d { "granted $granted" }
        }
    }
}