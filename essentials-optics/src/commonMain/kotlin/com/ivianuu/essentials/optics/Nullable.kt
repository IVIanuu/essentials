package com.ivianuu.essentials.optics

interface NullableBinding {
    fun <T> T?.bind(): T
}

inline fun <R> nullable(@BuilderInference block: NullableBinding.() -> R): R? = try {
    with(NullableBindingImpl, block)
} catch (e: NullableBindingImpl.ExitException) {
    null
}

@PublishedApi
internal object NullableBindingImpl : NullableBinding {
    override fun <T> T?.bind(): T = this ?: throw ExitException
    object ExitException : Exception()
}
