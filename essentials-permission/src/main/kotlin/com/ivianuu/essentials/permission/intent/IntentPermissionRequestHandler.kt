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

package com.ivianuu.essentials.permission.intent

import android.content.Intent
import com.ivianuu.essentials.permission.BindPermissionRequestHandler
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.util.StartActivityForResult
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.selects.select
import kotlin.time.milliseconds

val Permission.Companion.Intent by lazy {
    Permission.Key<Intent>(
        "Intent"
    )
}

@BindPermissionRequestHandler
@Transient
internal class IntentPermissionRequestHandler(
    private val permissionManager: PermissionManager,
    private val startActivityForResult: StartActivityForResult
) : PermissionRequestHandler {

    override fun handles(permission: Permission): Boolean =
        Permission.Intent in permission

    override suspend fun request(permission: Permission) {
        coroutineScope {
            select<Unit> {
                async {
                    startActivityForResult(permission[Permission.Intent])
                }.onAwait {}
                async {
                    while (!permissionManager.hasPermissions(permission).first()) {
                        delay(100.milliseconds)
                    }
                }.onAwait {}
            }.also { coroutineContext.cancelChildren() }
        }
    }

}
