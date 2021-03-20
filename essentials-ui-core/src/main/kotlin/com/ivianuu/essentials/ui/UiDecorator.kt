/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.ivianuu.essentials.ui.core.SystemBarManagerProvider
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.sortedTopological
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.typeKeyOf

// todo remove type parameter S once injekt is fixed
@Given
fun <@Given @ForTypeKey T : S, S : UiDecorator> uiDecoratorElement(
    @Given instance: T,
    @Given config: UiDecoratorConfig<S> = UiDecoratorConfig.DEFAULT
): UiDecoratorElement = UiDecoratorElement(
    typeKeyOf<T>(), instance as UiDecorator, config
)

data class UiDecoratorConfig<out T : UiDecorator>(
    val dependencies: Set<TypeKey<UiDecorator>> = emptySet(),
    val dependents: Set<TypeKey<UiDecorator>> = emptySet(),
) {
    companion object {
        val DEFAULT = UiDecoratorConfig<Nothing>(emptySet(), emptySet())
    }
}

typealias UiDecorator = @Composable (@Composable () -> Unit) -> Unit

data class UiDecoratorElement(
    val key: TypeKey<*>,
    val decorator: UiDecorator,
    val config: UiDecoratorConfig<*>
)

typealias DecorateUi = @Composable (@Composable () -> Unit) -> Unit

@Given
fun decorateUi(
    @Given elements: Set<UiDecoratorElement> = emptySet(),
    @Given logger: Logger
): DecorateUi = { content ->
    remember {
        elements
            .sortedTopological(
                key = { it.key },
                dependencies = { it.config.dependencies },
                dependents = { it.config.dependents }
            )
            .reversed()
            .fold(content) { acc, element ->
                {
                    logger.d { "Decorate ui ${element.key}" }
                    element.decorator(acc)
                }
            }
    }()
}

typealias AppTheme = UiDecorator

@Given
val appThemeConfig = UiDecoratorConfig<AppTheme>(dependencies = setOf(typeKeyOf<SystemBarManagerProvider>()))
