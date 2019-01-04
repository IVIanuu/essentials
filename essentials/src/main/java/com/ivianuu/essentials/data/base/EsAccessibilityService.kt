package com.ivianuu.essentials.data.base

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.serviceComponent
import com.ivianuu.injekt.modules
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

/**
 * Base accessibility service
 */
abstract class EsAccessibilityService : AccessibilityService(), InjektTrait {

    override val component by unsafeLazy {
        serviceComponent(this) {
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

    protected open fun modules() = emptyList<Module>()

}