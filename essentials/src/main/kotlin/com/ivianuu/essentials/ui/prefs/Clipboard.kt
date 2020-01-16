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

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.Composable
import androidx.ui.graphics.Image
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.common.asIconComposable
import com.ivianuu.essentials.ui.common.asTextComposable
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.util.Toaster

@Composable
fun ClipboardPreference(
    clipboardText: () -> String,
    dependencies: List<Dependency<*>>? = null,
    title: String? = null,
    summary: String? = null,
    image: Image? = null
) {
    ClipboardPreference(
        clipboardText = clipboardText,
        dependencies = dependencies,
        title = title?.asTextComposable(),
        summary = summary?.asTextComposable(),
        leading = image?.asIconComposable()
    )
}

@Composable
fun ClipboardPreference(
    clipboardText: () -> String,
    dependencies: List<Dependency<*>>? = null,
    title: @Composable() (() -> Unit)? = null,
    summary: @Composable() (() -> Unit)? = null,
    leading: @Composable() (() -> Unit)? = null,
    trailing: @Composable() (() -> Unit)? = null
) {
    val clipboardManager = inject<ClipboardManager>()
    val toaster = inject<Toaster>()
    SimplePreference(
        dependencies = dependencies,
        title = title,
        summary = summary,
        leading = leading,
        trailing = trailing,
        onClick = {
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText("", clipboardText())
            )
            toaster.toast(R.string.es_copied_to_clipboard)
        }
    )
}
