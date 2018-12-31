package com.ivianuu.essentials.injection

import com.ivianuu.injekt.*

infix fun <T : Any> Declaration<T>.bind(name: String) = apply {
    val newDeclaration = copy(key = Key(type, name), type = type, name = name)
    newDeclaration.kind = kind
    newDeclaration.override = override
    newDeclaration.createOnStart = createOnStart
    newDeclaration.attributes = attributes
    newDeclaration.definition = definition
    newDeclaration.instance = instance
    // omit newDeclaration.module = module because the module sets it
    module.declare(newDeclaration)
}

inline fun <reified T : Any> Module.bind(name: String) =
    factory<T>(name) { get() }