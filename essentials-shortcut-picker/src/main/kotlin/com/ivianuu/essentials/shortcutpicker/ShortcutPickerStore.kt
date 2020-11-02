/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.shortcutpicker

import com.ivianuu.essentials.shortcutpicker.ShortcutPickerAction.*
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.store.execute
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.essentials.util.startActivityForIntentResult
import com.ivianuu.injekt.Binding
import kotlinx.coroutines.CoroutineScope

@Binding
fun CoroutineScope.shortcutPickerStore(
    navigator: Navigator,
    getAllShortcuts: getAllShortcuts,
    extractShortcut: extractShortcut,
    startActivityForIntentResult: startActivityForIntentResult,
    showToastRes: showToastRes,
) = store<ShortcutPickerState, ShortcutPickerAction>(ShortcutPickerState()) {
    execute(
        block = { getAllShortcuts() },
        reducer = { copy(shortcuts = it) }
    )

    for (action in this) {
        when (action) {
            is PickShortcut -> {
                try {
                    val shortcutRequestResult = startActivityForIntentResult(action.shortcut.intent)
                        .data ?: continue
                    val shortcut = extractShortcut(shortcutRequestResult)
                    navigator.popTop(result = shortcut)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    showToastRes(R.string.es_failed_to_pick_shortcut)
                }
            }
        }.exhaustive
    }
}
