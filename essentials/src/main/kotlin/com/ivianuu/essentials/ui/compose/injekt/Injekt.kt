package com.ivianuu.essentials.ui.compose.injekt

import androidx.compose.Ambient
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ambient
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf

val ComponentAmbient = Ambient.of<Component>()

inline fun <reified T> ComponentComposition.inject(
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = inject<T>(typeOf(), name, parameters)

fun <T> ComponentComposition.inject(
    type: Type<T>,
    name: Any? = null,
    parameters: ParametersDefinition? = null
): T {
    val component = ambient(ComponentAmbient)
    return component.get(type, name, parameters)
}