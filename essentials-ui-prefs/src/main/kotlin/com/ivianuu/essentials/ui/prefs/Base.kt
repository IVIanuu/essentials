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

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.ivianuu.essentials.datastore.DataStore
import com.ivianuu.essentials.datastore.android.asState
import com.ivianuu.essentials.ui.common.interactive

fun Modifier.preferenceDependencies(
    vararg dependencies: Dependency<*>
): Modifier = composed {
    val dependenciesSatisfied = dependencies
        .map { dependency ->
            key(dependency) {
                val currentValue by dependency.dataStore.asState()
                currentValue == dependency.requiredValue
            }
        }
        .all { it }

    interactive(dependenciesSatisfied)
}

@Immutable
data class Dependency<T>(val dataStore: DataStore<T>, val requiredValue: T)

infix fun <T> DataStore<T>.requiresValue(requiredValue: T) = Dependency(this, requiredValue)