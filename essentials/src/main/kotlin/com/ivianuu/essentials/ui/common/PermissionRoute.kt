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

package com.ivianuu.essentials.ui.common

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.ivianuu.compose.ActivityAmbient
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.navigator
import com.ivianuu.compose.onActive

// todo Route + Navigator is the wrong api for this case

fun PermissionRoute(vararg permissions: String) = Route(isFloating = true) {
    val activity = ambient(ActivityAmbient) as FragmentActivity
    val navigator = navigator
    onActive {
        PermissionFragment.get(activity).requestPermissions(*permissions) {
            navigator.pop(it)
        }
    }
}

data class PermissionResult(
    val requestCode: Int,
    val permissions: Array<out String>,
    val grantResults: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PermissionResult) return false

        if (requestCode != other.requestCode) return false
        if (!permissions.contentEquals(other.permissions)) return false
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

class PermissionFragment : Fragment() {

    private val callbacksByResultCode = mutableMapOf<Int, (PermissionResult) -> Unit>()

    init {
        retainInstance = true
    }

    fun requestPermissions(
        vararg permissions: String,
        callback: (PermissionResult) -> Unit
    ) {
        val requestCode = nextResultCode()
        callbacksByResultCode[requestCode] = callback
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        callbacksByResultCode.remove(requestCode)
            ?.invoke(PermissionResult(requestCode, permissions, grantResults))
    }

    internal companion object {
        private val TAG = PermissionFragment::class.java.canonicalName
        fun get(activity: FragmentActivity): PermissionFragment {
            var fragment =
                activity.supportFragmentManager.findFragmentByTag(TAG) as? PermissionFragment
            if (fragment == null) {
                fragment = PermissionFragment()
                activity.supportFragmentManager.beginTransaction()
                    .add(fragment, TAG)
                    .commitNow()
            }

            return fragment
        }
    }
}