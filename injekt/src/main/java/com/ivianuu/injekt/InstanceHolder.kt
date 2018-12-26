package com.ivianuu.injekt

internal interface InstanceHolder<T : Any> {
    val declaration: Declaration<T>
    fun get(context: ComponentContext, params: Parameters): T
}

private object UNINITIALIZED_VALUE

internal class FactoryInstanceHolder<T : Any>(
    override val declaration: Declaration<T>
) : InstanceHolder<T> {

    override fun get(context: ComponentContext, params: Parameters) =
        declaration.binding(context, params)
            .also { debug { "Created instance for ${declaration.key}" } }

}

internal class SingleInstanceHolder<T : Any>(
    override val declaration: Declaration<T>
) : InstanceHolder<T> {

    private var _value: Any? = UNINITIALIZED_VALUE

    override fun get(context: ComponentContext, params: Parameters): T {
        if (_value === UNINITIALIZED_VALUE) {
            _value = declaration.binding(context, params)
            debug { "Created singleton instance for ${declaration.key}" }
        } else {
            debug { "Returning existing singleton instance for ${declaration.key}" }
        }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

}