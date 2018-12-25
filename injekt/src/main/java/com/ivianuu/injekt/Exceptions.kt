package com.ivianuu.injekt

open class InjektException(message: String, throwable: Throwable? = null) :
    RuntimeException(message, throwable)

open class ComponentException(message: String) : InjektException(message)

class ComponentNotInitializedException(message: String) : ComponentException(message)

class InjectionException(message: String) : InjektException(message)

class OverrideException(message: String) : InjektException(message) {
    constructor(
        override: Declaration<*>,
        existing: Declaration<*>
    ) : this("$override would override $existing")
}

class InstanceCreationException(message: String, cause: Throwable) : InjektException(message, cause)