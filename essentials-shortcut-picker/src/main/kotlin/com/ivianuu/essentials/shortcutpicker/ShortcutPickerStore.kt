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

import com.ivianuu.essentials.shortcutpicker.ShortcutPickerAction.PickShortcut
import com.ivianuu.essentials.store.iterator
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.ui.resource.resource
import com.ivianuu.essentials.ui.store.Initial
import com.ivianuu.essentials.ui.store.UiStoreBinding
import com.ivianuu.essentials.ui.store.reduceResource
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.essentials.util.startActivityForIntentResult
import kotlinx.coroutines.CoroutineScope

@UiStoreBinding
fun CoroutineScope.ShortcutPickerStore(
    extractShortcut: extractShortcut,
    getAllShortcuts: getAllShortcuts,
    initial: @Initial ShortcutPickerState = ShortcutPickerState(),
    navigator: Navigator,
    startActivityForIntentResult: startActivityForIntentResult,
    showToastRes: showToastRes,
) = store<ShortcutPickerState, ShortcutPickerAction>(initial) {
    reduceResource({ getAllShortcuts() }) { copy(shortcuts = it) }
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
        }
    }
}
