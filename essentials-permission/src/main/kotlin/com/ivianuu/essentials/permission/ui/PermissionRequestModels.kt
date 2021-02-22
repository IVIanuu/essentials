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

package com.ivianuu.essentials.permission.ui

import com.ivianuu.essentials.permission.Permission
import com.ivianuu.injekt.common.TypeKey

data class PermissionRequestKey(val permissionsKeys: List<TypeKey<Permission>>)

data class PermissionRequestState(val permissions: List<UiPermission<*>> = emptyList())

data class UiPermission<P : Permission>(
    val permissionKey: TypeKey<P>,
    val metadata: PermissionUiMetadata<P>
)

sealed class PermissionRequestAction {
    data class RequestPermission(val permission: UiPermission<*>) : PermissionRequestAction()
}
