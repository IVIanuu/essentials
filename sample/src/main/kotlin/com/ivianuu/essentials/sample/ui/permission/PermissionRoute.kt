package com.ivianuu.essentials.sample.ui.permission

import android.Manifest
import com.github.ajalt.timberkt.d
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.navigator
import com.ivianuu.compose.launchOnActive
import com.ivianuu.essentials.ui.common.PermissionResult
import com.ivianuu.essentials.ui.common.PermissionRoute
import com.ivianuu.essentials.ui.common.allGranted

fun PermissionRoute() = Route(isFloating = true) {
    val navigator = navigator
    launchOnActive {
        val granted = navigator.push<PermissionResult>(
            PermissionRoute(Manifest.permission.CAMERA)
        )
            ?.allGranted ?: false
        d {
            "permissions granted ? $granted"
        }
    }
}