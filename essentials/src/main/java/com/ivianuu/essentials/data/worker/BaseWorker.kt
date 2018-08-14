package com.ivianuu.essentials.data.worker

import android.content.Context
import androidx.work.Worker
import com.ivianuu.daggerextensions.work.WorkerInjection
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.rx.DisposableScopeProvider

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware, Injectable {

    override val providedContext: Context
        get() = applicationContext

    val scopeProvider = DisposableScopeProvider()

    final override fun doWork(): WorkerResult {
        if (this !is AutoInjector.Ignore) {
            WorkerInjection.inject(this)
        }

        return doRealWork()
    }

    override fun onStopped() {
        scopeProvider.dispose()
        super.onStopped()
    }

    protected abstract fun doRealWork(): WorkerResult
}