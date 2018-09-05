package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable

/**
 * Base accessibility service
 */
abstract class BaseAccessibilityService : AccessibilityService(), Injectable, LifecycleOwner {

    protected val disposables = CompositeDisposable()

    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)

    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        if (this !is AutoInjector.Ignore) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onStart(intent: Intent, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        disposables.clear()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }

    override fun getLifecycle(): Lifecycle = lifecycleDispatcher.lifecycle
}