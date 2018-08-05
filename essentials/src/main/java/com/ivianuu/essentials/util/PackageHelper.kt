package com.ivianuu.essentials.util

import android.app.Application
import com.ivianuu.essentials.util.ext.isAppInstalled
import javax.inject.Inject

/**
 * Package helper
 */
class PackageHelper @Inject constructor(private val app: Application) {

    fun isAppInstalled(packageName: String) = app.isAppInstalled(packageName)

}