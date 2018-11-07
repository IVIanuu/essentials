package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import dagger.android.AndroidInjection

/**
 * Base accessibility service
 */
abstract class BaseAccessibilityService : AccessibilityService() {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }
}