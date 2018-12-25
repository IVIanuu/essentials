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
        }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

}