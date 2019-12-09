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

import androidx.fragment.app.FragmentActivity

interface Permission {
    val metadata: Metadata
}

fun Permission(metadata: Metadata): Permission = SimplePermission(metadata = metadata)

private class SimplePermission(override val metadata: Metadata) : Permission {
    override fun toString() = "Permission($metadata)"
}

interface PermissionStateProvider {
    fun handles(permission: Permission): Boolean
    suspend fun isGranted(permission: Permission): Boolean
}

interface PermissionRequestHandler {
    fun handles(permission: Permission): Boolean
    suspend fun request(
        activity: FragmentActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult
}

data class PermissionResult(val isOk: Boolean)

interface PermissionRequestUi {
    fun performRequest(
        activity: FragmentActivity,
        manager: PermissionManager,
        request: PermissionRequest
    )
}

data class PermissionRequest(
    val id: String,
    val permissions: List<Permission>,
    val onComplete: () -> Unit
)