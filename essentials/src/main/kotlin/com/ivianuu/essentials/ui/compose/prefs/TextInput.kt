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

import android.graphics.drawable.Drawable
import android.text.InputType
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.res.stringResource
import com.afollestad.materialdialogs.input.input
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

@Composable
fun TextInputPreference(
    pref: Pref<String>,
    dialogTitle: String? = null,
    dialogHint: String? = null,
    dialogMessage: String? = null,
    dialogIcon: Drawable? = null,
    dialogPositiveButtonText: String? = +stringResource(R.string.es_ok),
    dialogNegativeButtonText: String? = +stringResource(R.string.es_cancel),
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    allowEmptyInput: Boolean = true,
    title: @Composable() () -> Unit,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    onChange: ((String) -> Boolean)? = null,
    enabled: Boolean = true,
    dependencies: List<Dependency<*>>? = null
) = composable("TextInputPreference:${pref.key}") {
    DialogPreference(
        pref = pref,
        buildDialog = { dismiss ->
            input(
                hint = dialogHint,
                prefill = pref.get(),
                inputType = inputType,
                allowEmpty = allowEmptyInput
            ) { _, input ->
                if (onChange?.invoke(input.toString()) != false) {
                    pref.set(input.toString())
                }
            }
        },
        dialogTitle = dialogTitle,
        dialogMessage = dialogMessage,
        dialogIcon = dialogIcon,
        dialogPositiveButtonText = dialogPositiveButtonText,
        dialogNegativeButtonText = dialogNegativeButtonText,
        title = title,
        summary = summary,
        leading = leading,
        onChange = onChange,
        enabled = enabled,
        dependencies = dependencies
    )
}