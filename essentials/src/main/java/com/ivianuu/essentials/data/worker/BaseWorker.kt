package com.ivianuu.essentials.data.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.WorkFinishedCallback
import androidx.work.Worker
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.worker.WorkerInjection
import com.ivianuu.essentials.util.ContextAware
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.Job

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware, Injectable {

    override val providedContext: Context
        get() = applicationContext

    protected val disposables = CompositeDisposable()
    protected val job = Job()

    @SuppressLint("RestrictedApi")
    override fun onStartWork(callback: WorkFinishedCallback) {
        if (shouldInject) {
            WorkerInjection.inject(this)
        }
        super.onStartWork(callback)
    }

    override fun onStopped(cancelled: Boolean) {
        disposables.clear()
        job.cancel()
        super.onStopped(cancelled)
    }
}