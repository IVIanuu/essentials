package com.ivianuu.essentials.data.app

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.util.ext.coroutinesIo
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Store for [AppInfo]'s
 */
// todo remove
class AppStore @Inject constructor(private val packageManager: PackageManager) {

    suspend fun installedApps() = withContext(coroutinesIo) {
        packageManager.getInstalledApplications(0)
            .asSequence()
            .map {
                AppInfo(
                    appName = it.loadLabel(packageManager).toString(),
                    packageName = it.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.toLowerCase() }
            .toList()
    }

    suspend fun launchableApps() = withContext(coroutinesIo) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        packageManager.queryIntentActivities(intent, 0)
            .asSequence()
            .map {
                AppInfo(
                    appName = it.loadLabel(packageManager).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.toLowerCase() }
            .toList()
    }

    suspend fun appInfo(packageName: String) = withContext(coroutinesIo) {
        AppInfo(
            packageName,
            packageManager.getApplicationInfo(packageName, 0).loadLabel(packageManager)
                .toString()
        )
    }
}