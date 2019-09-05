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

package com.ivianuu.essentials.ui.compose

import com.afollestad.materialdialogs.input.input
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ContextAmbient
import com.ivianuu.compose.ambient
import com.ivianuu.compose.state
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.kommon.core.content.string
import com.ivianuu.kprefs.Pref

fun ComponentComposition.TextInputDialogRoute(
    title: String? = null,
    titleRes: Int? = null,
    prefill: String? = null,
    prefillRes: Int? = null,
    hint: String? = null,
    hintRes: Int? = null,
    onInputCompleted: (String) -> Unit
) {
    val context = ambient(ContextAmbient)
    val (currentValue, setCurrentValue) = state {
        when {
            prefill != null -> prefill
            else -> context.string(prefillRes!!)
        }
    }

    Dialog {
        title(res = titleRes, text = title)
        positiveButton(res = R.string.es_ok) {
            onInputCompleted(currentValue)
        }
        negativeButton(res = R.string.es_cancel)

        input(
            hintRes = hintRes,
            hint = hint ?: "",
            prefill = prefill,
            waitForPositiveButton = false
        ) { _, input -> setCurrentValue(input.toString()) }
    }
}

/*
fun ComponentComposition.TextInputDialogRoute(
    title: String? = null,
    titleRes: Int? = null,
    hint: String? = null,
    hintRes: Int? = null,
    pref: Pref<String>,
    onChangePredicate: ((String) -> Boolean)? = null
) {
    TextInputDialog(
        title = title,
        titleRes = titleRes,
        hint = hint,
        hintRes = hintRes,
        prefill = pref.get(),
        onInputCompleted = {
            if (onChangePredicate?.invoke(it) ?: true) {
                pref.set(it)
            }
        }
    )
}*/