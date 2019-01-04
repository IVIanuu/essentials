package com.ivianuu.essentials.injection.multibinding

import com.ivianuu.injekt.*
import java.util.*

const val KEY_SET_BINDINGS = "setBindings"

fun <T : Any> ModuleContext.setBinding(setName: String) {
    factory(name = setName, override = true) {
        MultiBindingSet<T>(
            emptySet()
        )
    }
}

infix fun <T : Any, S : T> BeanDefinition<S>.intoSet(setName: String) = apply {
    attributes.getOrSet(KEY_SET_BINDINGS) { mutableSetOf<String>() }.add(setName)

    moduleContext.factory(name = setName, override = true) {
        component.beanRegistry
            .getAllDefinitions()
            .filter { it.attributes.get<Set<String>>(KEY_SET_BINDINGS)?.contains(setName) == true }
            .map { it as BeanDefinition<T> }
            .toSet()
            .let { MultiBindingSet(it) }
    }
}

inline fun <reified T : Any, reified S : T> ModuleContext.bindIntoSet(
    setName: String,
    declarationName: String? = null
) =
    factory<T>(UUID.randomUUID().toString()) { get<S>(declarationName) } intoSet setName