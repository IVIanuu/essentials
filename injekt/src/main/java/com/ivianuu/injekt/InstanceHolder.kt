package com.ivianuu.injekt

internal interface InstanceHolder<T : Any> {
    val declaration: Declaration<T>
    fun get(component: Component, params: Parameters): T

    fun create(component: Component, params: Parameters): T {
        return try {
            info { "Injecting instance for ${declaration.key}" }
            get(component, params)
        } catch (e: InjektException) {
            throw e
        } catch (e: Exception) {
            throw InstanceCreationException("Could not instantiate $declaration", e)
        }
    }
}

private object UNINITIALIZED_VALUE

internal class FactoryInstanceHolder<T : Any>(
    override val declaration: Declaration<T>
) : InstanceHolder<T> {

    override fun get(component: Component, params: Parameters) =
        declaration.provider(component, params)
            .also { debug { "Created instance for ${declaration.key}" } }

}

internal class SingleInstanceHolder<T : Any>(
    override val declaration: Declaration<T>
) : InstanceHolder<T> {

    private var _value: Any? = UNINITIALIZED_VALUE

    private val lock = this

    override fun get(component: Component, params: Parameters): T {
        val _v1 = _value
        if (_v1 !== UNINITIALIZED_VALUE) {
            debug { "Returning existing singleton instance for ${declaration.key}" }
            @Suppress("UNCHECKED_CAST")
            return _v1 as T
        }

        return synchronized(lock) {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                debug { "Returning existing singleton instance for ${declaration.key}" }
                @Suppress("UNCHECKED_CAST") (_v2 as T)
            } else {
                val typedValue = declaration.provider(component, params)
                _value = typedValue
                debug { "Created singleton instance for ${declaration.key}" }
                typedValue
            }
        }
    }

}