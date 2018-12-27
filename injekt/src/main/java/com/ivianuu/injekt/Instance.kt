package com.ivianuu.injekt

/**
 * The [Instance] of an [Declaration]
 */
abstract class Instance<T : Any>(val declaration: Declaration<T>) {

    internal lateinit var component: Component

    abstract fun get(params: Parameters): T

    fun create(params: Parameters): T {
        return try {
            info { "Injecting instance for ${declaration.key}" }
            get(params)
        } catch (e: InjektException) {
            throw e
        } catch (e: Exception) {
            throw InstanceCreationException("Could not instantiate $declaration", e)
        }
    }
}

internal class FactoryInstance<T : Any>(
    declaration: Declaration<T>
) : Instance<T>(declaration) {

    override fun get(params: Parameters) =
        declaration.provider.invoke(DeclarationBuilder(component), params)
            .also { debug { "Created instance for ${declaration.key}" } }

}

private object UNINITIALIZED_VALUE

internal class SingleInstance<T : Any>(
    declaration: Declaration<T>
) : Instance<T>(declaration) {

    private var _value: Any? = UNINITIALIZED_VALUE

    private val lock = this

    override fun get(params: Parameters): T {
        val _v1 = _value
        if (_v1 !== UNINITIALIZED_VALUE) {
            debug { "Returning existing singleton instance for ${declaration}" }
            @Suppress("UNCHECKED_CAST")
            return _v1 as T
        }

        return synchronized(lock) {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                debug { "Returning existing singleton instance for ${declaration.key}" }
                @Suppress("UNCHECKED_CAST") (_v2 as T)
            } else {
                val typedValue = declaration.provider.invoke(DeclarationBuilder(component), params)
                _value = typedValue
                debug { "Created singleton instance for ${declaration.key}" }
                typedValue
            }
        }
    }

}