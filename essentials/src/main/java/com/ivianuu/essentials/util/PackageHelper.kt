package com.ivianuu.essentials.util

import android.content.pm.PackageManager
import com.ivianuu.essentials.util.ext.isAppInstalled
import javax.inject.Inject

/**
 * Package helper
 */
class PackageHelper @Inject constructor(private val packageManager: PackageManager) {

    fun isAppInstalled(packageName: String) = packageManager.isAppInstalled(packageName)

    fun isAppLaunchable(packageName: String) =
        packageManager.getLaunchIntentForPackage(packageName) != null

    fun isAppEnabled(packageName: String) =
        packageManager.getApplicationInfo(packageName, 0).enabled

}