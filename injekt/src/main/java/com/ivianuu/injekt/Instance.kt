package com.ivianuu.injekt

/**
 * The [Instance] of an [Declaration]
 */
abstract class Instance<T : Any>(val declaration: Declaration<T>) {

    internal lateinit var component: Component

    abstract val isCreated: Boolean

    abstract fun get(params: Parameters): T

    fun create(params: Parameters): T {
        return try {
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

    override val isCreated: Boolean
        get() = false

    override fun get(params: Parameters) =
        declaration.provider.invoke(DeclarationBuilder(component), params)

}

private object UNINITIALIZED_VALUE

internal class SingleInstance<T : Any>(
    declaration: Declaration<T>
) : Instance<T>(declaration) {

    private var _value: Any? = UNINITIALIZED_VALUE

    private val lock = this

    override val isCreated: Boolean
        get() = _value !== UNINITIALIZED_VALUE

    override fun get(params: Parameters): T {
        val _v1 = _value
        if (_v1 !== UNINITIALIZED_VALUE) {
            @Suppress("UNCHECKED_CAST")
            return _v1 as T
        }

        return synchronized(lock) {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST") (_v2 as T)
            } else {
                val typedValue = declaration.provider.invoke(DeclarationBuilder(component), params)
                _value = typedValue
                typedValue
            }
        }
    }

}