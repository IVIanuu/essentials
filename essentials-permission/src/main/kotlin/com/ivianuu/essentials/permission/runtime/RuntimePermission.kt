/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.permission.runtime

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.util.ActivityResultLauncher
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext

interface RuntimePermission : Permission {
    val name: String
}

@Given
fun <P : RuntimePermission> runtimePermissionStateProvider(
    @Given context: AppContext
): PermissionStateProvider<P> = { permission ->
    context.checkSelfPermission(permission.name) == PackageManager.PERMISSION_GRANTED
}

@Given
fun <P : RuntimePermission> runtimePermissionRequestHandler(
    @Given activityResultLauncher: ActivityResultLauncher
): PermissionRequestHandler<P> = { permission ->
    activityResultLauncher.startActivityForResult(
        ActivityResultContracts.RequestPermission(),
        permission.name
    )
}
