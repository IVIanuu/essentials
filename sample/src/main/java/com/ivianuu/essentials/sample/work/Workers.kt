package com.ivianuu.essentials.sample.work

import android.content.Context
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.work.WorkerParameters
import com.ivianuu.essentials.hidenavbar.NavBarPrefs
import com.ivianuu.essentials.work.EsWorker
import com.ivianuu.essentials.work.bindWorker
import com.ivianuu.injekt.annotations.Factory
import com.ivianuu.injekt.annotations.Param
import com.ivianuu.injekt.module
import com.ivianuu.timberktx.d

val workerModule = module {
    bindWorker<MyWorkerOne>()
    bindWorker<MyWorkerTwo>()
}

@Factory
class MyWorkerOne(
    @Param context: Context,
    @Param workerParams: WorkerParameters,
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

@Factory
class MyWorkerTwo(
    @Param context: Context,
    @Param workerParams: WorkerParameters,
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