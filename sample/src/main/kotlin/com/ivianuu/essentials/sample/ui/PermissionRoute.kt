package com.ivianuu.essentials.sample.ui

import android.Manifest
import com.github.ajalt.timberkt.d
import com.ivianuu.compose.ambient
import com.ivianuu.compose.common.NavigatorAmbient
import com.ivianuu.compose.common.Route
import com.ivianuu.compose.common.launchOnActive
import com.ivianuu.essentials.ui.common.PermissionResult
import com.ivianuu.essentials.ui.common.PermissionRoute
import com.ivianuu.essentials.ui.common.allGranted

fun PermissionRoute() = Route(isFloating = true) {
    val navigator = ambient(NavigatorAmbient)
    launchOnActive {
        val granted = navigator.push<PermissionResult>(
            PermissionRoute(Manifest.permission.CAMERA)
        )?.allGranted ?: false
        d { "permissions granted ? $granted" }
        navigator.pop()
    }
}