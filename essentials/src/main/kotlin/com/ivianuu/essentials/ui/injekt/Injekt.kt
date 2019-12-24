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

package com.ivianuu.essentials.ui.injekt

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.remember
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf

val ComponentAmbient = Ambient.of<Component> { error("No component found") }

@Composable
inline fun <reified T> inject(
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = inject(type = typeOf<T>(), name = name, parameters = parameters)

@Composable
fun <T> inject(
    type: Type<T>,
    name: Any? = null,
    parameters: ParametersDefinition? = null
): T {
    val component = ambient(ComponentAmbient)
    return remember { component.get(type = type, name = name, parameters = parameters) }
}

@Composable
inline fun <reified T> injectOrNull(
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = injectOrNull(type = typeOf<T>(), name = name, parameters = parameters)

@Composable
fun <T> injectOrNull(
    type: Type<T>,
    name: Any? = null,
    parameters: ParametersDefinition? = null
): T? {
    val component = ambient(ComponentAmbient)
    return remember { component.getOrNull(type = type, name = name, parameters = parameters) }
}