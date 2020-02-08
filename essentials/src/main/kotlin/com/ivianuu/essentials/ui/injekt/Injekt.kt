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

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.staticAmbientOf
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.Parameters
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.emptyParameters
import com.ivianuu.injekt.typeOf

val ComponentAmbient =
    staticAmbientOf<Component> { error("No component found") }

@Composable
inline fun <reified T> inject(
    name: Any? = null,
    parameters: Parameters = emptyParameters()
) = inject(type = typeOf<T>(), name = name, parameters = parameters)

@Composable
fun <T> inject(
    type: Type<T>,
    name: Any? = null,
    parameters: Parameters = emptyParameters()
): T {
    val component = ComponentAmbient.current
    return remember { component.get(type = type, name = name, parameters = parameters) }
}
