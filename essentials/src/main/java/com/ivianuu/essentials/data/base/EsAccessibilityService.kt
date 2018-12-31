package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.injection.bindInstanceModule
import com.ivianuu.essentials.injection.serviceComponent


import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.*
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

/**
 * Base accessibility service
 */
abstract class EsAccessibilityService : AccessibilityService(), ComponentHolder {

    override val component by unsafeLazy {
        serviceComponent {
            dependencies(this@EsAccessibilityService.dependencies())
            modules(bindInstanceModule(this@EsAccessibilityService))
            modules(this@EsAccessibilityService.modules())
        }
    }

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
    }

    override fun onInterrupt() {
    }

    protected open fun dependencies() = emptyList<Component>()

    protected open fun modules() = emptyList<Module>()

}