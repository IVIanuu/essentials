package com.ivianuu.essentials.util

import android.content.pm.PackageManager
import com.ivianuu.essentials.util.ext.isAppInstalled
import javax.inject.Inject

/**
 * Package helper
 */
class PackageHelper @Inject constructor(private val packageManager: PackageManager) {

    fun isAppInstalled(packageName: String) = packageManager.isAppInstalled(packageName)

    fun isLaunchable(packageName: String) =
        packageManager.getLaunchIntentForPackage(packageName) != null

}