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

package com.ivianuu.essentials.ui.epoxy

import android.graphics.drawable.Drawable
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.epoxy.EpoxyController
import com.ivianuu.essentials.R
import com.ivianuu.kprefs.Pref

fun EpoxyController.EditTextDialogPrefListItem(
    pref: Pref<String>,
    id: Any? = pref.key,

    onSelectedPredicate: ((String) -> Boolean)? = null,

    title: String? = null,
    titleRes: Int = 0,

    text: String? = null,
    textRes: Int = 0,

    icon: Drawable? = null,
    iconRes: Int = 0,

    avatar: Drawable? = null,
    avatarRes: Int = 0,

    dialogTitle: String? = title,
    dialogTitleRes: Int = titleRes,

    dialogHint: String? = null,
    dialogHintRes: Int = 0,

    positiveDialogButtonText: String? = null,
    positiveDialogButtonTextRes: Int = R.string.es_ok,
    negativeDialogButtonText: String? = null,
    negativeDialogButtonTextRes: Int = R.string.es_cancel,

    dialogBlock: (MaterialDialog.() -> Unit)? = null,
    builderBlock: (FunModelBuilder.() -> Unit)? = null
) = EditTextDialogListItem(
    id = id,
    value = pref.get(),
    onInputCompleted = {
        if (onSelectedPredicate == null || onSelectedPredicate(it)) {
            pref.set(it)
        }
    },
    title = title,
    titleRes = titleRes,
    text = text,
    textRes = textRes,
    icon = icon,
    iconRes = iconRes,
    avatar = avatar,
    avatarRes = avatarRes,
    dialogTitle = dialogTitle,
    dialogTitleRes = dialogTitleRes,
    dialogHint = dialogHint,
    dialogHintRes = dialogHintRes,
    positiveDialogButtonText = positiveDialogButtonText,
    positiveDialogButtonTextRes = positiveDialogButtonTextRes,
    negativeDialogButtonText = negativeDialogButtonText,
    negativeDialogButtonTextRes = negativeDialogButtonTextRes,
    dialogBlock = dialogBlock,
    builderBlock = builderBlock
)