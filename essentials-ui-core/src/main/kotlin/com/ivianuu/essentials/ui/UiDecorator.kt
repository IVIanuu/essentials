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
import com.ivianuu.essentials.ui.core.ProvideSystemBarManager
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.sortedGraph
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.common.ForKey
import com.ivianuu.injekt.common.Key
import com.ivianuu.injekt.common.keyOf

@Qualifier annotation class UiDecoratorBinding
@Macro @GivenSetElement
fun <@ForKey T : @UiDecoratorBinding @Composable (@Composable () -> Unit) -> Unit> uiDecoratorBindingImpl(
    @Given instance: T,
    @Given config: UiDecoratorConfig<T> = UiDecoratorConfig.DEFAULT
): UiDecoratorElement = UiDecoratorElement(
    keyOf<T>(), instance as @Composable (@Composable () -> Unit) -> Unit, config
)

data class UiDecoratorConfig<out T : @Composable (@Composable () -> Unit) -> Unit>(
    val dependencies: Set<Key<@Composable (@Composable () -> Unit) -> Unit>> = emptySet(),
    val dependents: Set<Key<@Composable (@Composable () -> Unit) -> Unit>> = emptySet(),
) {
    companion object {
        val DEFAULT = UiDecoratorConfig<Nothing>(emptySet(), emptySet())
    }
}

data class UiDecoratorElement(
    val key: Key<*>,
    val instance: @Composable (@Composable () -> Unit) -> Unit,
    val config: UiDecoratorConfig<*>
)

@GivenFun
@Composable
fun DecorateUi(
    @Given decorators: Set<UiDecoratorElement>,
    @Given logger: Logger,
    content: @Composable () -> Unit
) {
    remember {
        decorators
            .sortedGraph(
                key = { it.key },
                dependencies = { it.config.dependencies },
                dependents = { it.config.dependents }
            )
            .reversed()
            .fold(content) { acc, decorator ->
                {
                    logger.d { "Decorate ui ${decorator.key}" }
                    decorator.instance(acc)
                }
            }
    }()
}

@Qualifier annotation class AppThemeBinding

typealias AppTheme = @Composable (@Composable () -> Unit) -> Unit

@Macro @UiDecoratorBinding
fun <T : @AppThemeBinding AppTheme> appThemeBindingImpl(@Given instance: T) =
    instance

@Given fun appThemeConfigBindingImpl() =
    UiDecoratorConfig<AppTheme>(dependencies = setOf(keyOf<ProvideSystemBarManager>()))
