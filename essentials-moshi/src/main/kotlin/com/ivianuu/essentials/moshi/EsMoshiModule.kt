package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule
import com.ivianuu.injekt.merge.MergeInto
import com.squareup.moshi.Moshi

@BindingModule(ApplicationComponent::class)
annotation class JsonAdapterBinding {
    class ModuleImpl<T : Any> {
        @SetElements
        fun invoke(instance: T): JsonAdapters = setOf(instance)
    }
}

typealias JsonAdapters = Set<Any>

@MergeInto(ApplicationComponent::class)
@Module
object EsMoshiModule {

    @Binding(ApplicationComponent::class)
    fun moshi(jsonAdapters: JsonAdapters): Moshi = Moshi.Builder()
        .apply {
            jsonAdapters
                .forEach { adapter -> add(adapter) }
        }
        .build()

    @SetElements
    fun defaultAdapters(): JsonAdapters = emptySet()

}
