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
import androidx.ui.graphics.Image
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.compose.box.BoxWrapper
import com.ivianuu.essentials.ui.compose.box.unfoldBox
import com.ivianuu.essentials.ui.compose.common.asIconComposable
import com.ivianuu.essentials.ui.compose.common.asTextComposable
import com.ivianuu.essentials.ui.compose.common.framed
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.composableWithKey
import com.ivianuu.essentials.ui.compose.core.remember
import com.ivianuu.essentials.ui.compose.material.SimpleListItem

@Composable
fun <T> PreferenceWrapper(
    box: Box<T>,
    onChange: ((T) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    preference: @Composable() (PreferenceContext<T>) -> Unit
) = composableWithKey("Preference:$box") {
    Dependencies(dependencies = dependencies ?: emptyList()) { dependenciesOk ->
        val context = remember(box) { PreferenceContext(box) }
        context.boxWrapper = unfoldBox(box)
        context.onChange = onChange
        context.enabled = enabled
        context.dependenciesOk = dependenciesOk

        val finalEnabled = enabled && dependenciesOk
        Opacity(if (finalEnabled) 1f else 0.5f) {
            preference(context)
        }
    }
}

@Composable
fun PreferenceLayout(
    title: String? = null,
    summary: String? = null,
    image: Image? = null,
    onClick: (() -> Unit)? = null
) = composable {
    PreferenceLayout(
        title = title.asTextComposable(),
        summary = summary.asTextComposable(),
        leading = image.asIconComposable(),
        onClick = onClick
    )
}

@Composable
fun PreferenceLayout(
    title: (@Composable() () -> Unit)? = null,
    summary: (@Composable() () -> Unit)? = null,
    leading: (@Composable() () -> Unit)? = null,
    trailing: (@Composable() () -> Unit)? = null,
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
        val isOk = shouldBeEnabled && onChange?.invoke(newValue) ?: true
        if (isOk) currentValue = newValue
        return isOk
    }
}