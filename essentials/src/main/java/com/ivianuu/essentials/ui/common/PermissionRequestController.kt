/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.director.activitycallbacks.addPermissionResultListener
import com.ivianuu.director.activitycallbacks.requestPermissions
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.util.ext.ResultKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.injekt.inject
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PermissionRequestKey(
    val permissions: Set<String>,
    override var resultCode: Int = 0
) : ControllerKey(::PermissionRequestController, NavOptions().dialog()), ResultKey

/**
 * Permission request controller
 */
class PermissionRequestController : EsController() {

    private val key by inject<PermissionRequestKey>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addPermissionResultListener(key.resultCode) { requestCode, permissions, grantResults ->
                travelerRouter.goBackWithResult(
                    key.resultCode,
                    PermissionResult(requestCode, permissions.toSet(), grantResults)
                )
            }
            requestPermissions(key.permissions.toTypedArray(), key.resultCode)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View = View(activity) // dummy

    override fun onAttach(view: View) {
        super.onAttach(view)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            travelerRouter.goBackWithResult(
                key.resultCode, PermissionResult(
                    key.resultCode,
                    key.permissions,
                    key.permissions
                        .map { PackageManager.PERMISSION_GRANTED }
                        .toIntArray()
                ))
        }
    }

}

class PermissionResult(
    val requestCode: Int,
    val permissions: Set<String>,
    val grantResults: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PermissionResult) return false

        if (requestCode != other.requestCode) return false
        if (permissions != other.permissions) return false
        if (!grantResults.contentEquals(other.grantResults)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = requestCode
        result = 31 * result + permissions.hashCode()
        result = 31 * result + grantResults.contentHashCode()
        return result
    }
}

val PermissionResult.allGranted: Boolean get() = grantResults.all { it == PackageManager.PERMISSION_GRANTED }