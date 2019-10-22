package com.ivianuu.essentials.ui.compose.injekt

import androidx.compose.Ambient
import androidx.compose.ambient
import androidx.compose.effectOf
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.get

val ComponentAmbient = Ambient.of<Component> { error("No component found") }

inline fun <reified T> inject(
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = effectOf<T> {
    val component = +ambient(ComponentAmbient)
    return@effectOf component.get<T>(name, parameters)
}