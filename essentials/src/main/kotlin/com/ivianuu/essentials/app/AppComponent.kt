package com.ivianuu.essentials.app

import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait

object AppComponent {
    private lateinit var _component: Component

    val isInitialized: Boolean get() = this::_component.isInitialized

    fun get(): Component = _component

    fun init(component: Component) {
        _component = component
    }
}

interface AppComponentInjektTrait : InjektTrait {
    override val component: Component
        get() = AppComponent.get()
}