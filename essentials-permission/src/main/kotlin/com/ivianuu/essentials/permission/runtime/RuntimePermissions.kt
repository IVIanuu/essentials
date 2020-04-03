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
import androidx.fragment.app.FragmentActivity
import com.ivianuu.essentials.permission.MetaDataKeyWithValue
import com.ivianuu.essentials.permission.Metadata
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.PermissionRequestHandler
import com.ivianuu.essentials.permission.PermissionResult
import com.ivianuu.essentials.permission.PermissionStateProvider
import com.ivianuu.essentials.permission.bindPermissionRequestHandlerIntoSet
import com.ivianuu.essentials.permission.bindPermissionStateProviderIntoSet
import com.ivianuu.essentials.permission.metadataOf
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.atomic.AtomicInteger

fun RuntimePermission(
    name: String,
    vararg metadata: MetaDataKeyWithValue<*>
) = Permission(
    metadata = metadataOf(
        Metadata.RuntimePermissionName withValue name,
        *metadata
    )
)

val Metadata.Companion.RuntimePermissionName by lazy {
    Metadata.Key<String>(
        "RuntimePermissionName"
    )
}

@ApplicationScope
@Module
private fun ComponentBuilder.runtimePermission() {
    bindPermissionStateProviderIntoSet<RuntimePermissionStateProvider>()
    bindPermissionRequestHandlerIntoSet<RuntimePermissionRequestHandler>()
}

@Factory
private class RuntimePermissionStateProvider(
    private val context: Context
) : PermissionStateProvider {

    override fun handles(permission: Permission): Boolean =
        Metadata.RuntimePermissionName in permission.metadata

    override suspend fun isGranted(permission: Permission): Boolean =
        context.checkSelfPermission(permission.metadata[Metadata.RuntimePermissionName]) ==
                PackageManager.PERMISSION_GRANTED
}

@Factory
private class RuntimePermissionRequestHandler : PermissionRequestHandler {
    override fun handles(permission: Permission): Boolean =
        Metadata.RuntimePermissionName in permission.metadata

    override suspend fun request(
        activity: FragmentActivity,
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

        val granted = fragment.request(permission.metadata[Metadata.RuntimePermissionName])
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
