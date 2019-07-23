/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui.widget3

interface CompositionContext {
    fun emit(component: Component)
}

abstract class Component(
    val type: Any,
    val key: Any? = null
) {

    private var _parent: Component? = null
    val parent: Component? = null

    private val _children = mutableListOf<Component>()
    val children: List<Component>
        get() = _children

    private inner class ComponentContext : CompositionContext {
        override fun emit(component: Component) {
            _children.add(component)
            component.mount(this@Component)
        }
    }

    protected open fun mount(parent: Component?) {
        _parent = parent
    }

    protected open fun unmount() {
    }

    protected open fun CompositionContext.build() {
    }
}