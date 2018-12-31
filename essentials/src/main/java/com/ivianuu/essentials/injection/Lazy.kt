package com.ivianuu.essentials.injection

import com.ivianuu.injekt.ComponentDefinition
import com.ivianuu.injekt.component

fun lazyComponent(
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition
) = lazy { component(createEagerInstances, definition) }