package com.ivianuu.essentials.injection

import com.ivianuu.injekt.ComponentDefinition
import com.ivianuu.injekt.component

fun lazyComponent(
    name: String? = null,
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition
) = lazy { component(name, createEagerInstances, definition) }