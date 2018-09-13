package com.ivianuu.essentials.data.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Base service
 */
abstract class BaseService : Service(), CoroutineScope, Injectable {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    protected val disposables = CompositeDisposable()

    private val job = Job()

    override fun onCreate() {
        if (shouldInject) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        disposables.clear()
        job.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null
}