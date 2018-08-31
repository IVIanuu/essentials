package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.AutoInjector
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService() {

    protected val disposables = CompositeDisposable()
    protected val connectedDisposables = CompositeDisposable()

    override fun onCreate() {
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    override fun onListenerDisconnected() {
        connectedDisposables.clear()
        super.onListenerDisconnected()
    }

}