package com.ivianuu.essentials.hidenavbar

import android.os.Build

data class NavBarConfig(
    val hidden: Boolean,
    val rotationMode: NavBarRotationMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        NavBarRotationMode.NOUGAT
    } else {
        NavBarRotationMode.MARSHMALLOW
    },
    val showWhileScreenOff: Boolean = true
)