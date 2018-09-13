package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.CancellableCoroutineScope
import com.ivianuu.essentials.util.coroutines.cancelCoroutineScope
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope

/**
 * Base accessibility service
 */
abstract class BaseAccessibilityService : AccessibilityService(),
    CoroutineScope by CancellableCoroutineScope(), Injectable {

    val disposables = CompositeDisposable()

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

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }
}