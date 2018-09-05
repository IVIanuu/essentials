package com.ivianuu.essentials.data.base

import android.content.Intent
import android.os.IBinder
import android.service.notification.NotificationListenerService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService(), Injectable,
    LifecycleOwner {

    protected val disposables = CompositeDisposable()
    protected val connectedDisposables = CompositeDisposable()

    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)

    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        disposables.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    override fun onListenerDisconnected() {
        connectedDisposables.clear()
        super.onListenerDisconnected()
    }

    override fun getLifecycle(): Lifecycle = lifecycleDispatcher.lifecycle
}