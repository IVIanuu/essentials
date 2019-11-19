/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.compose.injekt

import androidx.compose.Ambient
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.effect
import com.ivianuu.essentials.ui.compose.core.memo
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf

val ComponentAmbient = Ambient.of<Component> { error("No component found") }

inline fun <reified T> inject(
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = inject(type = typeOf<T>(), name = name, parameters = parameters)

fun <T> inject(
    type: Type<T>,
    name: Any? = null,
    parameters: ParametersDefinition? = null
): T = effect {
    val component = ambient(ComponentAmbient)
    return@effect memo { component.get(type = type, name = name, parameters = parameters) }
}