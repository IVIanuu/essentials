package com.ivianuu.essentials.data.worker

import android.content.Context
import androidx.work.Worker
import com.ivianuu.essentials.util.ContextAware
import io.reactivex.disposables.CompositeDisposable

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware {

    override val providedContext: Context
        get() = applicationContext

    protected val disposables = CompositeDisposable()

    override fun onStopped(cancelled: Boolean) {
        disposables.clear()
        super.onStopped(cancelled)
    }
}