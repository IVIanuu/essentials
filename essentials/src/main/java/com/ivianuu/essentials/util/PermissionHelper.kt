package com.ivianuu.essentials.util

import android.content.Context
import com.ivianuu.essentials.util.ext.hasPermissions
import javax.inject.Inject

/**
 * Permission helper
 */
class PermissionHelper @Inject constructor(private val context: Context) {

    fun hasPermissions(vararg permissions: String) =
        context.hasPermissions(*permissions)
}