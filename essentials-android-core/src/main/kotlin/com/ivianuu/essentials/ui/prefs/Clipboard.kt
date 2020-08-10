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

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManagerAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.text.AnnotatedString
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.material.ListItem

@Composable
fun ClipboardListItem(
    clipboardText: () -> String,
    title: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = ContextAmbient.current
    val clipboardManager = ClipboardManagerAmbient.current
    ListItem(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        leading = leading,
        trailing = trailing,
        onClick = {
            clipboardManager.setText(AnnotatedString(clipboardText()))
            Toast.makeText(context, R.string.es_copied_to_clipboard, Toast.LENGTH_SHORT).show()
        }
    )
}
