package com.ivianuu.essentials.moshi.android

import com.ivianuu.essentials.moshi.bindJsonAdapterIntoSet
import com.ivianuu.injekt.ComponentBuilder

fun ComponentBuilder.esMoshiAndroid() {
    bindJsonAdapterIntoSet<ColorJsonAdapter>()
    bindJsonAdapterIntoSet<PxJsonAdapter>()
    bindJsonAdapterIntoSet<DpJsonAdapter>()
    bindJsonAdapterIntoSet<IntPxJsonAdapter>()
}
