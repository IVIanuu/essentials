package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.essentials.util.ext.isAppInstalled
import javax.inject.Inject

/**
 * Package helper
 */
class PackageHelper @Inject constructor(private val context: Context) {

    fun isAppInstalled(packageName: String) = context.isAppInstalled(packageName)

}