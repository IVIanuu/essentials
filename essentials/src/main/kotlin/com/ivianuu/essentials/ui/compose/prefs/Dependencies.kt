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

package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.coroutines.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Composable
fun Dependencies(
    dependencies: List<Dependency<*>>,
    child: @Composable() (Boolean) -> Unit
) {
    val dependenciesOkFlow: Flow<Boolean> = remember(dependencies) {
        dependencies.asDependencyFlow()
    }
    val dependenciesOk = collect(dependenciesOkFlow, false)
    child(dependenciesOk)
}

data class Dependency<T : Any>(
    val box: Box<T>,
    val value: T
)

private fun List<Dependency<*>>?.asDependencyFlow(): Flow<Boolean> {
    if (this.isNullOrEmpty()) return flowOf(true)

    val flows =
        map { dependency ->
            dependency.box.asFlow()
                .map { currentValue -> currentValue == dependency.value }
        }
            .toTypedArray()

    return combine(*flows) { values -> values.all { it } }
}
