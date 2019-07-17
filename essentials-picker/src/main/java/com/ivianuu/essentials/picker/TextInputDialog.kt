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

package com.ivianuu.essentials.picker

import com.afollestad.materialdialogs.input.input
import com.ivianuu.essentials.ui.dialog.dialogRoute

fun textInputRoute(
    title: String? = null,
    titleRes: Int? = null,
    hint: String? = null,
    hintRes: Int? = null,
    prefill: String? = null,
    prefillRes: Int? = null,
    inputType: Int = -1,
    allowEmpty: Boolean = false
) = dialogRoute { navigator ->
    noAutoDismiss()
    title(res = titleRes, text = title)
    input(
        hint = hint,
        hintRes = hintRes,
        prefill = prefill,
        prefillRes = prefillRes,
        inputType = inputType,
        allowEmpty = allowEmpty
    ) { _, input -> navigator.pop(input.toString()) }
    positiveButton(R.string.es_ok)
    negativeButton(R.string.es_cancel) { navigator.pop() }
}