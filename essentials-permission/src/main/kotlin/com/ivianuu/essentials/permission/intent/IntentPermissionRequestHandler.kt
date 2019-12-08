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
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.MetadataKeys
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionActivity
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionResult
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.delay

@Factory
class IntentPermissionRequestHandler : PermissionRequestHandler {

    override fun handles(permission: Permission): Boolean =
        permission.metadata.contains(MetadataKeys.Intent)

    override suspend fun request(
        activity: PermissionActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult {
        val fragment = RequestFragment()
        fragment.permissionManager = manager

        activity.supportFragmentManager.beginTransaction()
            .add(fragment, RequestFragment.TAG)
            .commitNow()

        val granted = fragment.request(permission.metadata[MetadataKeys.Intent], permission)
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


val MetadataKeys.Intent by lazy {
    Metadata.Key<Intent>(
        "Intent"
    )
}