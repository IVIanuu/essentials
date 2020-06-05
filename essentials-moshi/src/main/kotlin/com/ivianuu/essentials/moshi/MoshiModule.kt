package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.BindingEffectFunction
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped
import com.ivianuu.injekt.set
import com.squareup.moshi.Moshi

@BindingEffect(ApplicationComponent::class)
annotation class BindJsonAdapter

@BindingEffectFunction(BindJsonAdapter::class)
@Module
fun <T : Any> jsonAdapter() {
    set<@JsonAdapters Set<Any>, Any> {
        add<T>()
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class JsonAdapters

@Module
fun esMoshiModule() {
    installIn<ApplicationComponent>()
    set<@JsonAdapters Set<Any>, Any>()
    scoped { adapters: @JsonAdapters Set<Any> ->
        Moshi.Builder()
            .apply { adapters.forEach { adapter -> add(adapter) } }
            .build()!!
    }
}
