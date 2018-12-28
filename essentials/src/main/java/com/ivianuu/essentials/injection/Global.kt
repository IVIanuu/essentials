package com.ivianuu.essentials.injection

import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentHolder

val globalComponent get() = GlobalComponentHolder.component

object GlobalComponentHolder : ComponentHolder {

    override val component: Component
        get() = _component ?: error("Global component not initialized")

    private var _component: Component? = null

    fun initialize(component: Component) {
        if (_component != null) {
            error("Cannot initialize global component twice")
        }
        _component = component
    }
}