package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.scoped
import com.ivianuu.injekt.set
import com.squareup.moshi.Moshi

@BindingEffect(ApplicationComponent::class)
annotation class BindJsonAdapter {
    companion object {
        @Module
        operator fun <T : Any> invoke() {
            set<@JsonAdapters Set<Any>, Any> {
                add<T>()
            }
        }
    }
}

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class JsonAdapters

@Module
fun EsMoshiModule() {
    installIn<ApplicationComponent>()
    set<@JsonAdapters Set<Any>, Any>()
    scoped {
        Moshi.Builder()
            .apply {
                get<@JsonAdapters Set<Any>>()
                    .forEach { adapter -> add(adapter) }
            }
            .build()!!
    }
}
