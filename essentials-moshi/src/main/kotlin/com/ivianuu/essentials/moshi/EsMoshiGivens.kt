package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElements
import com.ivianuu.injekt.given
import com.squareup.moshi.Moshi

@Effect
annotation class GivenJsonAdapter {
    companion object {
        @GivenSetElements
        operator fun <T : Any> invoke(): JsonAdapters = setOf(given<T>())
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

    @GivenSetElements
    fun defaultAdapters(): JsonAdapters = emptySet()

}
