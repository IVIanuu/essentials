package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ApplicationStorage
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given
import com.squareup.moshi.Moshi

@Effect
annotation class GivenJsonAdapter {
    companion object {
        @SetElements
        operator fun <T : Any> invoke(): JsonAdapters = setOf(given<T>())
    }
}

typealias JsonAdapters = Set<Any>

object EsMoshiModule {

    @Given(ApplicationStorage::class)
    fun moshi(): Moshi = Moshi.Builder()
        .apply {
            given<JsonAdapters>()
                .forEach { adapter -> add(adapter) }
        }
        .build()

    @SetElements
    fun defaultAdapters(): JsonAdapters = emptySet()

}
