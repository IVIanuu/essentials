/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import android.view.inputmethod.InputMethodManager
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.state
import com.ivianuu.essentials.getOrNull
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import com.ivianuu.injekt.inject
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest

@JvmInline value class KeyboardVisibleProvider(val keyboardVisible: Flow<Boolean>)

context(AccessibilityEvent.Provider, KeyboardHeightProvider, NamedCoroutineScope<AppScope>)
    @Provide fun keyboardVisibleProvider(): @Scoped<AppScope> KeyboardVisibleProvider =
  KeyboardVisibleProvider(
    accessibilityEvents
      .filter {
        it.isFullScreen &&
            it.className == "android.inputmethodservice.SoftInputWindow"
      }
      .onStart<Any?> { emit(Unit) }
      .transformLatest {
        emit(true)
        while ((getKeyboardHeight() ?: 0) > 0) {
          delay(100)
        }
        emit(false)
        awaitCancellation()
      }
      .distinctUntilChanged()
      .state(SharingStarted.WhileSubscribed(1000), false)
  )

@Provide val keyboardVisibilityAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )

fun interface KeyboardHeightProvider {
  fun getKeyboardHeight(): Int?
}

context(InputMethodManager) @Provide fun keyboardHeightProvider() = KeyboardHeightProvider {
  catch {
    val method = InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
    method.invoke(inject<InputMethodManager>()) as Int
  }.getOrNull()
}
