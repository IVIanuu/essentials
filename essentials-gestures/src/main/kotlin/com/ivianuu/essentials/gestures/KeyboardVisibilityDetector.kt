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
import com.ivianuu.essentials.accessibility.AccessibilityComponent
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.injekt.Single
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest
import java.lang.reflect.Method

/**
 * Provides info about the keyboard state
 */
@ApplicationScope
@Single
class KeyboardVisibilityDetector(
    private val inputMethodManager: InputMethodManager
) : AccessibilityComponent() {

    override val config: AccessibilityConfig
        get() = AccessibilityConfig(
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        )

    private val softInputChanges = EventFlow<Unit>()

    val keyboardVisible: Flow<Boolean>
        get() {
            return softInputChanges
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
        }

    private fun getKeyboardHeight(): Int {
        return try {
            val method = getInputMethodWindowVisibleHeightMethod
                ?: inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
                    .also { getInputMethodWindowVisibleHeightMethod = it }
            method.invoke(inputMethodManager) as Int
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!event.isFullScreen) return
        if (event.className != "android.inputmethodservice.SoftInputWindow") return
        softInputChanges.offer(Unit)
    }

    private companion object {
        private var getInputMethodWindowVisibleHeightMethod: Method? = null
    }
}
