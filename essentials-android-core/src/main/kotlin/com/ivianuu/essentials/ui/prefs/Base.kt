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

package com.ivianuu.essentials.ui.prefs

import androidx.compose.Immutable
import androidx.compose.key
import androidx.ui.core.Modifier
import androidx.ui.core.composed
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.asState
import com.ivianuu.essentials.ui.common.interactive

fun Modifier.preferenceDependencies(
    vararg dependencies: Dependency<*>
): Modifier = composed {
    val dependenciesSatisfied = dependencies
        .map {
            key(it) {
                it.box.asState().value == it.value
            }
        }
        .all { it }

    interactive(dependenciesSatisfied)
}

@Immutable
data class Dependency<T>(val box: Box<T>, val value: T)
