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

package com.ivianuu.essentials.permission.runtime

import android.content.Context
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.MetadataKeys
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionActivity
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionResult
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.atomic.AtomicInteger

fun RuntimePermission(
    name: String,
    vararg pairs: Pair<Metadata.Key<*>, Any?>
) = Permission(
    metadata = metadataOf(
        MetadataKeys.RuntimePermissionName to name,
        *pairs
    )
)

val MetadataKeys.RuntimePermissionName by lazy {
    Metadata.Key<String>(
        "RuntimePermissionName"
    )
}

@Factory
class RuntimePermissionStateProvider(
    private val context: Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        permission.metadata.contains(MetadataKeys.RuntimePermissionName)

    override suspend fun isGranted(permission: Permission): Boolean =
        context.checkSelfPermission(permission.metadata[MetadataKeys.RuntimePermissionName]) ==
                PackageManager.PERMISSION_GRANTED

}

@Factory
class RuntimePermissionRequestHandler : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        permission.metadata.contains(MetadataKeys.RuntimePermissionName)

    override suspend fun request(
        activity: PermissionActivity,
        manager: PermissionManager,
        permission: Permission
    ): PermissionResult {
        val fragment = RequestFragment()

        activity.supportFragmentManager.beginTransaction()
            .add(
                fragment,
                RequestFragment.TAG
            )
            .commitNow()

        val granted = fragment.request(permission.metadata[MetadataKeys.RuntimePermissionName])
        return PermissionResult(isOk = granted)
    }

    class RequestFragment : Fragment() {

        private val permissionResult = CompletableDeferred<Boolean>()

        init {
            retainInstance = true
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            permissionResult.complete(
                grantResults.first() == PackageManager.PERMISSION_GRANTED
            )
        }

        suspend fun request(name: String): Boolean {
            requestPermissions(arrayOf(name), requestCodes.getAndIncrement())
            return permissionResult.await()
        }

        internal companion object {
            const val TAG = "RequestFragment"
        }
    }
}

private val requestCodes = AtomicInteger(0)