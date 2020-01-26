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
import androidx.compose.Immutable
import androidx.compose.Pivotal
import androidx.ui.res.stringResource
import com.ivianuu.essentials.R
import com.ivianuu.essentials.store.Box
import com.ivianuu.essentials.ui.common.asRenderableComposable
import com.ivianuu.essentials.ui.common.asTextComposable
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.dialog.SingleChoiceListDialog
import com.ivianuu.essentials.ui.painter.Renderable

@Composable
fun <T> SingleChoiceListPreference(
    @Pivotal box: Box<T>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Renderable? = null,
    dialogTitle: String? = title,
    items: List<SingleChoiceListPreference.Item<T>>
) {
    SingleChoiceListPreference(
        valueController = ValueController(box),
        enabled = enabled,
        dependencies = dependencies,
        title = title.asTextComposable(),
        summary = summary.asTextComposable(),
        leading = image.asRenderableComposable(),
        dialogTitle = dialogTitle.asTextComposable(),
        items = items
    )
}

@Composable
fun <T> SingleChoiceListPreference(
    valueController: ValueController<T>,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable (() -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    dialogTitle: @Composable (() -> Unit)? = title,
    items: List<SingleChoiceListPreference.Item<T>>
) {
    DialogPreference(
        valueController = valueController,
        enabled = enabled,
        dependencies = dependencies,
        title = title?.let { { title() } },
        summary = summary?.let { { summary() } },
        leading = leading?.let { { leading() } },
        dialog = { context, dismiss ->
            SingleChoiceListDialog(
                items = items,
                selectedItem = items.first { it.value == context.currentValue },
                onSelect = { context.setIfOk(it.value) },
                item = { Text(it.title) },
                title = dialogTitle,
                negativeButton = { DialogCloseButton(stringResource(R.string.es_cancel)) }
            )
        }
    )
}

object SingleChoiceListPreference {
    @Immutable
    data class Item<T>(
        val title: String,
        val value: T
    )
}
