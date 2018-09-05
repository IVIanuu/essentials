package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.injection.AutoInjector
import com.ivianuu.essentials.injection.Injectable
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable

/**
 * Base accessibility service
 */
abstract class BaseAccessibilityService : AccessibilityService(), Injectable {

    protected val disposables = CompositeDisposable()

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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }
}