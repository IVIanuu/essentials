package com.ivianuu.essentials.moshi.android

import com.ivianuu.essentials.moshi.bindJsonAdapterIntoSet
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.esMoshiAndroidModule() {
    bindJsonAdapterIntoSet<ColorJsonAdapter>()
    bindJsonAdapterIntoSet<PxJsonAdapter>()
    bindJsonAdapterIntoSet<DpJsonAdapter>()
    bindJsonAdapterIntoSet<IntPxJsonAdapter>()
}
