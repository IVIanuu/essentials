package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.CancellableCoroutineScope
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService(), CoroutineScope,
    Injectable {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    protected val disposables = CompositeDisposable()

    private val job = Job()

    protected val connectedDisposables = CompositeDisposable()

    protected var connectedCoroutineScope = CancellableCoroutineScope()
        private set

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

    override fun onListenerConnected() {
        super.onListenerConnected()
        connectedCoroutineScope = CancellableCoroutineScope()
    }

    override fun onListenerDisconnected() {
        connectedDisposables.clear()
        connectedCoroutineScope.cancel()
        super.onListenerDisconnected()
    }
}