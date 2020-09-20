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

package com.ivianuu.essentials.gestures

import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest

@Reader
val keyboardVisible: Flow<Boolean>
    get() = given<AccessibilityServices>().events
        .onEach {
            given<AccessibilityServices>().applyConfig(
                AccessibilityConfig(
                    eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                )
            )
        }
        .filter {
            it.isFullScreen &&
                    it.className == "android.inputmethodservice.SoftInputWindow"
        }
        .map { Unit }
        .onStart { emit(Unit) }
        .transformLatest {
            while (true) {
                emit(Unit)
                delay(100)
            }
        }
        .map { getKeyboardHeight() }
        .map { it > 0 }
        .distinctUntilChanged()

@Reader
private fun getKeyboardHeight(): Int {
    return try {
        val inputMethodManager = given<InputMethodManager>()
        val method = inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
        method.invoke(inputMethodManager) as Int
    } catch (t: Throwable) {
        t.printStackTrace()
        -1
    }
}
