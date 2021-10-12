/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.systemoverlay

import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.app.Eager
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.scope.AppScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest

typealias KeyboardVisible = Boolean

@Provide fun keyboardVisible(
  accessibilityEvents: Flow<AccessibilityEvent>,
  keyboardHeightProvider: KeyboardHeightProvider,
  scope: NamedCoroutineScope<AppScope>
): @Eager<AppScope> Flow<KeyboardVisible> = accessibilityEvents
  .filter {
    it.isFullScreen &&
        it.className == "android.inputmethodservice.SoftInputWindow"
  }
  .onStart<Any?> { emit(Unit) }
  .transformLatest {
    emit(true)
    while ((keyboardHeightProvider() ?: 0) > 0) {
      delay(100)
    }
    emit(false)
    awaitCancellation()
  }
  .distinctUntilChanged()
  .stateIn(scope, SharingStarted.WhileSubscribed(1000), false)

@Provide val keyboardVisibilityAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )

private typealias KeyboardHeightProvider = () -> Int?

@Provide fun keyboardHeightProvider(
  inputMethodManager: @SystemService InputMethodManager
): KeyboardHeightProvider = {
  catch {
    val method = inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
    method.invoke(inputMethodManager) as Int
  }.getOrNull()
}
