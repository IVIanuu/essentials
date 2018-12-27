package com.ivianuu.essentials.sample.data

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ivianuu.assistedinject.Assisted
import com.ivianuu.assistedinject.AssistedFactory
import com.ivianuu.assistedinject.AssistedInject
import com.ivianuu.essentials.work.InjectWorkerFactory
import com.ivianuu.timberktx.d

/**
 * @author Manuel Wrage (IVIanuu)
 */
@AssistedFactory(MyOtherWorker.Factory::class)
class MyOtherWorker @AssistedInject constructor(
    @Assisted context: Context,
    private val activityManager: ActivityManager,
    @Assisted private val workerParams: WorkerParameters,
    private val packageManager: PackageManager
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        d { "dowork" }
        return Result.success()
    }

    interface Factory : InjectWorkerFactory
}