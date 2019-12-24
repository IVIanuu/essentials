/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.processrestart

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.unsafeLazy
import com.ivianuu.injekt.Factory

@Factory
class ProcessRestarter(
    private val activityManager: ActivityManager,
    private val buildInfo: BuildInfo,
    private val context: Context,
    private val packageManager: PackageManager
) {

    val isRestartProcess by unsafeLazy {
        val currentPid: Int = android.os.Process.myPid()
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses =
            manager.runningAppProcesses?.any {
                it.pid == currentPid && it.processName == ":restartprocess"
            }
        return@unsafeLazy false
    }

    fun restartProcess(intent: Intent = getMainIntent()) {
        d { "restart process" }
        ProcessRestartActivity.launch(context, intent)
        Runtime.getRuntime().exit(0)
    }

    private fun getMainIntent(): Intent =
        packageManager.getLaunchIntentForPackage(buildInfo.packageName)!!
            .addFlags(FLAG_ACTIVITY_NEW_TASK)
            .addFlags(FLAG_ACTIVITY_CLEAR_TASK)
}