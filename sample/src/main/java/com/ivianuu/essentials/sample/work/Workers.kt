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

package com.ivianuu.essentials.sample.work

import android.content.Context
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.work.EsWorker
import com.ivianuu.essentials.work.worker
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module

val workerModule = module {
    worker { context, workerParams -> MyWorkerOne(context, workerParams, get(), get()) }
    worker { context, workerParams -> MyWorkerTwo(context, workerParams, get()) }
}

class MyWorkerOne(
    context: Context,
    workerParams: WorkerParameters,
    windowManager: WindowManager,
    navBarPrefs: NavBarPrefs
) : EsWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        d { "do work" }
        return Result.success()
    }
}

class MyWorkerTwo(
    context: Context,
    workerParams: WorkerParameters,
    packageManager: PackageManager
) : EsWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        d { "do work" }
        return Result.success()
    }
}