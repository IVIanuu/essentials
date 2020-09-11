package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.Adapter
import com.ivianuu.injekt.common.ApplicationContext
import com.ivianuu.injekt.given
import com.ivianuu.injekt.keyOf
import com.squareup.moshi.Moshi

@Adapter
annotation class GivenJsonAdapter {
    companion object : Adapter.Impl<Any> {
        override fun ContextBuilder.configure(key: Key<Any>, provider: @Reader () -> Any) {
            set(keyOf<JsonAdapters>()) {
                add(key, elementProvider = provider)
            }
        }
    }
}

typealias JsonAdapters = Set<Any>

object EsMoshiGivens {

    @Given(ApplicationContext::class)
    fun moshi(): Moshi = Moshi.Builder()
        .apply {
            given<JsonAdapters>()
                .forEach { adapter -> add(adapter) }
        }
        .build()

    @Given
    fun defaultAdapters(): JsonAdapters = emptySet()

}
