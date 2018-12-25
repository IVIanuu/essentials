package com.ivianuu.injekt

/**
 * @author Manuel Wrage (IVIanuu)
 */
internal interface InstanceHolder<T : Any> {
    val declaration: Declaration<T>
    fun get(context: ComponentContext, params: Parameters): T
}

private object UNINITIALIZED_VALUE

internal class FactoryInstanceHolder<T : Any>(
    override val declaration: Declaration<T>
) : InstanceHolder<T> {

    override fun get(context: ComponentContext, params: Parameters) =
        declaration.provider(context, params)
            .also { debug { "Created instance for ${declaration.key}" } }

}

internal class SynchronizedSingleInstanceHolder<T : Any>(
    override val declaration: Declaration<T>
) : InstanceHolder<T> {

    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    private val lock = this

    override fun get(context: ComponentContext, params: Parameters): T {
        var value = _value
        if (value !== UNINITIALIZED_VALUE) {
            @Suppress("UNCHECKED_CAST")
            debug { "Returning existing singleton instance for ${declaration.key}" }
            return value as T
        }

        return synchronized(lock) {
            value = _value
            if (value !== UNINITIALIZED_VALUE) {
                debug { "Returning existing singleton instance for ${declaration.key}" }
                @Suppress("UNCHECKED_CAST") value as T
            } else {
                val typedValue = declaration.provider(context, params)
                debug { "Created singleton instance for ${declaration.key}" }
                _value = typedValue
                typedValue
            }
        }
    }
}

internal class UnsafeSingleInstanceHolder<T : Any>(
    override val declaration: Declaration<T>
) : InstanceHolder<T> {

    private var _value: Any? = UNINITIALIZED_VALUE

    override fun get(context: ComponentContext, params: Parameters): T {
        if (_value === UNINITIALIZED_VALUE) {
            _value = declaration.provider(context, params)
        }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

}