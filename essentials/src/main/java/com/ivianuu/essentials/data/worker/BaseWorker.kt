package com.ivianuu.essentials.data.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.WorkFinishedCallback
import androidx.work.Worker
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.injection.worker.WorkerInjection
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.coroutines.CancellableCoroutineScope
import com.ivianuu.essentials.util.coroutines.cancelCoroutineScope
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware, CoroutineScope by CancellableCoroutineScope(),
    Injectable {

    override val providedContext: Context
        get() = applicationContext

    protected val disposables = CompositeDisposable()

    @SuppressLint("RestrictedApi")
    override fun onStartWork(callback: WorkFinishedCallback) {
        if (shouldInject) {
            WorkerInjection.inject(this)
        }
        super.onStartWork(callback)
    }

    override fun onStopped(cancelled: Boolean) {
        disposables.clear()
        cancelCoroutineScope()
        super.onStopped(cancelled)
    }
}