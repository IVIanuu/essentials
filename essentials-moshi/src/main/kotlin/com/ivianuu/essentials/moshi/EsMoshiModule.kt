package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given
import com.squareup.moshi.Moshi

@Effect
annotation class JsonAdapter {
    companion object {
        @SetElements(ApplicationComponent::class)
        operator fun <T : Set<Any>> invoke(): JsonAdapters = setOf(given<T>())
    }
}

@Distinct
typealias JsonAdapters = Set<Any>

object EsMoshiModule {

    @Given(ApplicationComponent::class)
    fun moshi(): Moshi = Moshi.Builder()
        .apply {
            given<JsonAdapters>()
                .forEach { adapter -> add(adapter) }
        }
        .build()

}
