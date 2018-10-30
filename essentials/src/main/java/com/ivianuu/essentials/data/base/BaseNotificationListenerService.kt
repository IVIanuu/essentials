package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import dagger.android.AndroidInjection

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService(), Injectable {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val connectedScope: Scope get() = _connectedScope
    private var _connectedScope = MutableScope()

    override fun onCreate() {
        if (shouldInject) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        _connectedScope = MutableScope()
    }

    override fun onListenerDisconnected() {
        _connectedScope.close()
        super.onListenerDisconnected()
    }
}