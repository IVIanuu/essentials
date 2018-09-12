package com.ivianuu.essentials.util.ext

import android.content.pm.PackageManager

fun PackageManager.isAppInstalled(packageName: String) = try {
    getApplicationInfo(packageName, 0)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}

fun PackageManager.isAppLaunchable(packageName: String) =
    getLaunchIntentForPackage(packageName) != null

fun PackageManager.isAppEnabled(packageName: String) = try {
    getApplicationInfo(packageName, 0).enabled
} catch (e: Exception) {
    false
}