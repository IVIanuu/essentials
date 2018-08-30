package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.util.rx.disposableScopeProvider
import dagger.android.AndroidInjection

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService() {

    val scopeProvider = disposableScopeProvider()
    val connectedScopeProvider = disposableScopeProvider()

    override fun onCreate() {
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        scopeProvider.dispose()
        super.onDestroy()
    }

    override fun onListenerDisconnected() {
        connectedScopeProvider.dispose()
        super.onListenerDisconnected()
    }

}