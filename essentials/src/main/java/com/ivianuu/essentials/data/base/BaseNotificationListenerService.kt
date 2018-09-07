package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.Job

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService(), Injectable {

    protected val disposables = CompositeDisposable()
    protected val job = Job()

    protected val connectedDisposables = CompositeDisposable()
    protected var connectedJob = Job()
        private set

    override fun onCreate() {
        if (this !is AutoInjector.Ignore) {
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
        if (connectedJob.isCompleted) {
            connectedJob = Job()
        }
    }

    override fun onListenerDisconnected() {
        connectedDisposables.clear()
        connectedJob.cancel()
        super.onListenerDisconnected()
    }
}