/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample

import android.content.Intent
import android.content.pm.PackageManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Store for [AppInfo]'s
 */
class AppStore(private val packageManager: PackageManager) {

    fun installedApps(): Observable<List<AppInfo>> {
        return Observable.fromCallable {
            val launchableApps = packageManager.getInstalledApplications(0)
            return@fromCallable launchableApps
                .map {
                    AppInfo(
                        appName = it.loadLabel(packageManager).toString(),
                        packageName = it.packageName
                    )
                }
                .distinctBy(AppInfo::packageName)
                .sortedBy { it.appName.toLowerCase() }
        }.subscribeOn(Schedulers.io())
    }

    fun launchableApps(): Observable<List<AppInfo>> {
        return Observable.fromCallable {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val launchableApps = packageManager.queryIntentActivities(intent, 0)
            return@fromCallable launchableApps
                .map {
                    AppInfo(
                        appName = it.loadLabel(packageManager).toString(),
                        packageName = it.activityInfo.packageName
                    )
                }
                .distinctBy(AppInfo::packageName)
                .sortedBy { it.appName.toLowerCase() }
        }//.subscribeOn(Schedulers.io())
    }

    fun getAppInfo(packageName: String): Single<AppInfo> {
        return Single.fromCallable {
            val info = packageManager.getApplicationInfo(packageName, 0)
            AppInfo(
                appName = info.loadLabel(packageManager).toString(),
                packageName = packageName
            )
        }
    }
}

/**
 * Simple application info
 */
data class AppInfo(
    val packageName: String,
    val appName: String
)