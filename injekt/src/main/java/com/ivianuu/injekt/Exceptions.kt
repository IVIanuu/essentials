package com.ivianuu.injekt

open class InjektException(message: String, throwable: Throwable? = null) :
    RuntimeException(message, throwable)

class InjectionException(message: String) : InjektException(message)

class OverrideException(message: String) : InjektException(message) {
    constructor(
        override: Declaration<*>,
        existing: Declaration<*>
    ) : this("$override would override $existing")
}

class InstanceCreationException(message: String, cause: Throwable) : InjektException(message, cause)