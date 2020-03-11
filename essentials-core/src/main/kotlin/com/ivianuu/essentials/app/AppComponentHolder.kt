package com.ivianuu.essentials.app

import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentOwner

object AppComponentHolder : ComponentOwner {
    private lateinit var _component: Component
    override val component: Component
        get() = _component

    val isInitialized: Boolean get() = this::_component.isInitialized

    fun init(component: Component) {
        check(!this::_component.isInitialized)
        _component = component
    }
}

interface AppComponentOwner : ComponentOwner {
    override val component: Component
        get() = AppComponentHolder.component
}
