package com.ivianuu.essentials.moshi

import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.KeyOverload
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.set
import com.ivianuu.injekt.get
import com.ivianuu.injekt.single
import com.squareup.moshi.Moshi

@ApplicationScope
@Module
private fun ComponentBuilder.esMoshiModule() {
    set<Any>(setQualifier = JsonAdapters)

    single {
        val adapters = get<Set<Any>>(qualifier = JsonAdapters)
        Moshi.Builder()
            .apply { adapters.forEach { adapter -> add(adapter) } }
            .build()!!
    }
}

@KeyOverload
fun <T : Any> ComponentBuilder.bindJsonAdapterIntoSet(
    adapterKey: Key<T>
) {
    set<Any>(setQualifier = JsonAdapters) { add(adapterKey) }
}

@QualifierMarker
annotation class JsonAdapters {
    companion object : Qualifier.Element
}
