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
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.sortedGraph
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import com.ivianuu.injekt.GivenSetElement

fun <T : @Composable (@Composable () -> Unit) -> Unit> uiDecoratorBinding(
    key: String,
    dependencies: Set<String> = emptySet(),
    dependents: Set<String> = emptySet()
): @GivenSetElement (@Given T) -> UiDecorator = { content ->
    UiDecorator(
        key = key,
        dependencies = dependencies,
        dependents = dependents,
        content = content as @Composable (@Composable () -> Unit) -> Unit
    )
}

data class UiDecorator(
    val key: String,
    val dependencies: Set<String>,
    val dependents: Set<String>,
    val content: @Composable (@Composable () -> Unit) -> Unit
)

@GivenFun
@Composable
fun DecorateUi(
    @Given decorators: Set<UiDecorator>,
    @Given logger: Logger,
    content: @Composable () -> Unit
) {
    remember {
        decorators
            .sortedGraph(
                key = { it.key },
                dependencies = { it.dependencies },
                dependents = { it.dependents }
            )
            .reversed()
            .fold(content) { acc, decorator ->
                {
                    logger.d { "Decorate ui ${decorator.key}" }
                    decorator.content(acc)
                }
            }
    }()
}

fun <T : @Composable (@Composable () -> Unit) -> Unit> appThemeBinding() =
    uiDecoratorBinding<T>(
        key = "app_theme",
        // The theme typically uses EsMaterialTheme which itself uses the SystemBarManager
        // So we ensure that were running after the system bars decorator
        dependencies = setOf("system_bars")
    )
