package com.ivianuu.essentials

inline fun <R> nullable(@BuilderInference block: NullableBinding.() -> R): R? = try {
  with(NullableBindingImpl, block)
} catch (e: NullableBindingImpl.ShortCircuitException) {
  null
}

interface NullableBinding {
  fun <T> T?.bind(): T
}

@PublishedApi
internal object NullableBindingImpl : NullableBinding {
  override fun <T> T?.bind(): T = this ?: throw ShortCircuitException

  object ShortCircuitException : ControlException()
}
