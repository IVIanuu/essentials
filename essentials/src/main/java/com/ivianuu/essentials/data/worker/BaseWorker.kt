package com.ivianuu.essentials.data.worker

import android.content.Context
import androidx.work.Worker
import com.ivianuu.essentials.util.ContextAware
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.Job

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware {

    override val providedContext: Context
        get() = applicationContext

    protected val disposables = CompositeDisposable()
    protected val job = Job()

    override fun onStopped(cancelled: Boolean) {
        disposables.clear()
        job.cancel()
        super.onStopped(cancelled)
    }
}