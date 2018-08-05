package com.ivianuu.essentials.data.app

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.util.ext.IO
import io.reactivex.Single
import javax.inject.Inject

/**
 * Store for [AppInfo]'s
 */
class AppStore @Inject constructor(
    private val packageManager: PackageManager
) {

    fun installedApps(): Single<List<AppInfo>> = Single.fromCallable {
        packageManager.getInstalledApplications(0)
            .map {
                AppInfo(
                    appName = it.loadLabel(packageManager).toString(),
                    packageName = it.packageName
                )
            }
            .distinctBy(AppInfo::packageName)
            .sortedBy { it.appName.toLowerCase() }
    }.subscribeOn(IO)

    fun launchableApps(): Single<List<AppInfo>> = Single.fromCallable {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        packageManager.queryIntentActivities(intent, 0)
            .map {
                AppInfo(
                    appName = it.loadLabel(packageManager).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
            .distinctBy(AppInfo::packageName)
            .sortedBy { it.appName.toLowerCase() }
    }.subscribeOn(IO)
}