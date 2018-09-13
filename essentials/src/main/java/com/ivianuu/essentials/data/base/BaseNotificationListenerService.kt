package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.CancellableCoroutineScope
import com.ivianuu.essentials.util.coroutines.cancelCoroutineScope
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService(),
    CoroutineScope by CancellableCoroutineScope(), Injectable {

    protected val disposables = CompositeDisposable()

    protected val connectedDisposables = CompositeDisposable()

    protected val connectedCoroutineScope: CoroutineScope
        get() = _connectedCoroutineScope
    protected var _connectedCoroutineScope = CancellableCoroutineScope()
        private set

    override fun onCreate() {
        if (shouldInject) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        disposables.clear()
        cancelCoroutineScope()
        super.onDestroy()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        _connectedCoroutineScope = CancellableCoroutineScope()
    }

    override fun onListenerDisconnected() {
        connectedDisposables.clear()
        _connectedCoroutineScope.cancel()
        super.onListenerDisconnected()
    }
}