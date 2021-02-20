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
import com.ivianuu.essentials.util.sortedGraph
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForKey
import com.ivianuu.injekt.common.Key
import com.ivianuu.injekt.common.keyOf

@Qualifier
annotation class UiDecoratorBinding

@Macro
@GivenSetElement
fun <T : @UiDecoratorBinding S, @ForKey S : UiDecorator> uiDecoratorBindingImpl(
    @Given instance: T,
    @Given config: UiDecoratorConfig<S> = UiDecoratorConfig.DEFAULT
): UiDecoratorElement = UiDecoratorElement(
    keyOf<S>(), instance as UiDecorator, config
)

data class UiDecoratorConfig<out T : UiDecorator>(
    val dependencies: Set<Key<UiDecorator>> = emptySet(),
    val dependents: Set<Key<UiDecorator>> = emptySet(),
) {
    companion object {
        val DEFAULT = UiDecoratorConfig<Nothing>(emptySet(), emptySet())
    }
}

typealias UiDecorator = @Composable (@Composable () -> Unit) -> Unit

data class UiDecoratorElement(
    val key: Key<*>,
    val decorator: UiDecorator,
    val config: UiDecoratorConfig<*>
)

typealias DecorateUi = @Composable (@Composable () -> Unit) -> Unit

@Given
fun decorateUi(
    @Given elements: Set<UiDecoratorElement>,
    @Given logger: Logger
): DecorateUi = { content ->
    remember {
        elements
            .sortedGraph(
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

@Qualifier
annotation class AppThemeBinding

typealias AppTheme = UiDecorator

@Suppress("USELESS_CAST")
@UiDecoratorBinding
@Macro
@Given
fun <T : @AppThemeBinding AppTheme> appThemeBindingImpl(@Given instance: T): AppTheme =
    instance as UiDecorator

@Given
fun appThemeConfigBindingImpl() =
    UiDecoratorConfig<AppTheme>(dependencies = setOf(keyOf<SystemBarManagerProvider>()))
