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

import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.remember
import androidx.ui.core.drawOpacity
import androidx.ui.foundation.Box
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.box.unfoldBox
import com.ivianuu.essentials.ui.material.ListItem

@Composable
fun <T> ValueController(
    box: Box<T>,
    onRequestValueChange: (T) -> Boolean = { true }
): ValueController<T> {
    val wrapper = unfoldBox(box)
    return remember<ValueController<T>> {
        object : ValueController<T> {
            override val currentValue: T
                get() = wrapper.value

            override fun setValue(value: T) {
                if (onRequestValueChange(value)) wrapper.value = value
            }

            override fun canSetValue(value: T): Boolean = onRequestValueChange.invoke(value)
        }
    }
}

@Composable
fun <T> PreferenceWrapper(
    valueController: ValueController<T>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    preference: @Composable (PreferenceContext<T>) -> Unit
) {
    Dependencies(dependencies = dependencies ?: emptyList()) { dependenciesOk ->
        val context = remember { PreferenceContext<T>() }
        context.valueController = valueController
        context.enabled = enabled
        context.dependenciesOk = dependenciesOk

        val finalEnabled = enabled && dependenciesOk
        Box(modifier = drawOpacity(if (finalEnabled) 1f else 0.5f)) {
            preference(context)
        }
    }
}

@Composable
fun PreferenceLayout(
    onClick: (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    ListItem(
        onClick = onClick,
        title = title,
        subtitle = summary,
        leading = leading,
        trailing = trailing
    )
}

interface ValueController<T> {
    val currentValue: T
    fun setValue(value: T)
    fun canSetValue(value: T): Boolean
}

fun <T> ValueController(
    value: T,
    canSetValue: (T) -> Boolean = { true },
    onValueChange: (T) -> Unit
) = object : ValueController<T> {
    override val currentValue: T
        get() = value

    override fun setValue(value: T) {
        onValueChange(value)
    }

    override fun canSetValue(value: T): Boolean = canSetValue.invoke(value)
}

@Model
class PreferenceContext<T> {

    internal var valueController: ValueController<T>? = null // todo lateinit

    val currentValue: T get() = valueController!!.currentValue

    var dependenciesOk = false
        internal set
    var enabled = false
        internal set
    val shouldBeEnabled: Boolean get() = enabled && dependenciesOk

    fun setIfOk(newValue: T): Boolean {
        val isOk = shouldBeEnabled
        if (isOk) valueController!!.setValue(newValue)
        return isOk
    }
}
