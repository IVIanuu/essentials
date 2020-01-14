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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionResult
import com.ivianuu.essentials.permission.bindPermissionRequestHandler
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import kotlinx.coroutines.delay

val Metadata.Companion.Intent by lazy {
    Metadata.Key<Intent>(
        "Intent"
    )
}

internal val EsIntentPermissionRequestHandlerModule = Module {
    bindPermissionRequestHandler<IntentPermissionRequestHandler>()
}

@Factory
internal class IntentPermissionRequestHandler : PermissionRequestHandler {

    override fun handles(permission: Permission): Boolean =
        Metadata.Intent in permission.metadata

    override suspend fun request(
        activity: FragmentActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult {
        val fragment = RequestFragment()
        fragment.permissionManager = manager

        activity.supportFragmentManager.beginTransaction()
            .add(fragment, RequestFragment.TAG)
            .commitNow()

        val granted = fragment.request(permission.metadata[Metadata.Intent], permission)
        return PermissionResult(isOk = granted)
    }

    class RequestFragment : Fragment() {

        lateinit var permissionManager: PermissionManager

        init {
            retainInstance = true
        }

        suspend fun request(intent: Intent, permission: Permission): Boolean {
            startActivity(intent)

            while (!permissionManager.hasPermissions(permission)) {
                delay(100)
            }

            startActivity(requireActivity().intent)

            return true
        }

        companion object {
            const val TAG = "RequestFragment"
        }
    }
}
