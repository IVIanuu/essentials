package com.ivianuu.essentials.util.ext

import android.content.pm.PackageManager

fun PackageManager.isAppInstalled(packageName: String) = try {
    getApplicationInfo(packageName, 0)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}