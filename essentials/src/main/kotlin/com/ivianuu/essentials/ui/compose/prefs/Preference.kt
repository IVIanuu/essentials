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
import androidx.ui.core.Opacity
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.compose.box.BoxWrapper
import com.ivianuu.essentials.ui.compose.box.unfoldBox
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.coroutines.collect
import com.ivianuu.essentials.ui.compose.material.SimpleListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Composable
fun <T> PreferenceWrapper(
    box: Box<T>,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    preference: @Composable() (PreferenceContext<T>) -> Unit
) = composableWithKey("Preference:$box") {
    val context = remember(box) { PreferenceContext(box) }

    val dependenciesOkFlow: Flow<Boolean> = remember(dependencies) {
        dependencies.asFlow()
    }
    val dependenciesOk = collect(dependenciesOkFlow, false)
    context.boxWrapper = unfoldBox(box)
    context.onChange = onChange
    context.enabled = enabled
    context.dependenciesOk = dependenciesOk

    Opacity(if (dependenciesOk) 1f else 0.5f) {
        val finalEnabled = enabled && dependenciesOk
        Opacity(if (finalEnabled) 1f else 0.5f) {
            preference(context)
        }
    }
}

@Composable
fun PreferenceLayout(
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    trailing: @Composable() (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) = composable {
    SimpleListItem(
        title = title,
        subtitle = summary,
        leading = leading,
        trailing = trailing,
        onClick = onClick
    )
}

class PreferenceContext<T>(
    val box: Box<T>
) {

    internal lateinit var boxWrapper: BoxWrapper<T>

    var currentValue: T
        get() = boxWrapper.value
        set(value) {
            boxWrapper.value = value
        }

    var onChange: ((T) -> Boolean)? by framed(null)
        internal set
    var dependenciesOk by framed(false)
        internal set
    var enabled by framed(false)
        internal set
    val shouldBeEnabled: Boolean get() = enabled && dependenciesOk

    fun setIfOk(newValue: T): Boolean {
        val isOk = onChange?.invoke(newValue) ?: true
        if (isOk) currentValue = newValue
        return isOk
    }
}

data class Dependency<T : Any>(
    val box: Box<T>,
    val value: T
)

private fun List<Dependency<*>>?.asFlow(): Flow<Boolean> {
    if (this == null) return flowOf(true)

    val flows =
        map { dependency ->
            dependency.box.asFlow()
                .map { currentValue -> currentValue == dependency.value }
        }
            .toTypedArray()

    return combine(*flows) { values -> values.all { it } }
}