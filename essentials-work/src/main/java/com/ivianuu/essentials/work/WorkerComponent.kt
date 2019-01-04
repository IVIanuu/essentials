package com.ivianuu.essentials.work

import androidx.work.Worker
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.instanceModule


/**
 * Returns a [Component] with convenient configurations
 */
fun <T : Worker> workerComponent(
    instance: T,
    name: String? = instance.javaClass.simpleName + "Component",
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
) = component(name, createEagerInstances) {
    instance.parentComponentOrNull()?.let { components(it) }
    modules(instanceModule(instance), workerModule(instance))
    definition?.invoke(this)
}

/**
 * Returns the parent [Component] if available or null
 */
fun Worker.parentComponentOrNull() = (applicationContext as? InjektTrait)?.component

/**
 * Returns the parent [Component] or throws
 */
fun Worker.parentComponent() = parentComponentOrNull() ?: error("No parent found for $this")

const val WORKER = "worker"

/**
 * Returns a [Module] with convenient declarations
 */
fun <T : Worker> workerModule(
    instance: T,
    name: String? = "WorkerModule"
) = module(name) {
    // worker
    factory(WORKER) { instance as Worker }
}

fun DefinitionContext.worker() = get<Worker>(WORKER)