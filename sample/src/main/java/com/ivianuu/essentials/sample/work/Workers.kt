package com.ivianuu.essentials.sample.work

import android.content.Context
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.work.WorkerParameters
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.work.EsWorker
import com.ivianuu.essentials.work.worker
import com.ivianuu.injekt.get
import com.ivianuu.injekt.module
import com.ivianuu.timberktx.d

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

    init {
        d { "hello $windowManager, $navBarPrefs" }
    }

    override fun doWork(): Result {
        d { "do work" }
        return Result.success()
    }
}

class MyWorkerTwo(
    context: Context,
    workerParams: WorkerParameters,
    packageManager: PackageManager
) : EsWorker(context, workerParams) {

    init {
        d { "hello $packageManager" }
    }

    override fun doWork(): Result {
        d { "do work" }
        return Result.success()
    }
}