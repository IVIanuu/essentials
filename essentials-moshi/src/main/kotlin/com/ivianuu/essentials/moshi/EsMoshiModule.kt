package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given
import com.squareup.moshi.Moshi

@Effect
annotation class JsonAdapter {
    companion object {
        @SetElements
        operator fun <T : Any> invoke(): JsonAdapters = setOf(given<T>())
    }
}

typealias JsonAdapters = Set<Any>

object EsMoshiModule {

    @Given(ApplicationScoped::class)
    fun moshi(): Moshi = Moshi.Builder()
        .apply {
            given<JsonAdapters>()
                .forEach { adapter -> add(adapter) }
        }
        .build()

    @SetElements
    fun defaultAdapters(): JsonAdapters = emptySet()

}
