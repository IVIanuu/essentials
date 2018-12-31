package com.ivianuu.essentials.work

import androidx.work.Worker
import com.ivianuu.injekt.ComponentDefinition
import com.ivianuu.injekt.ComponentHolder
import com.ivianuu.injekt.component

fun Worker.workerComponent(
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
) = component(createEagerInstances) {
    (applicationContext as? ComponentHolder)?.let { dependencies(it.component) }
    definition?.invoke(this)
}