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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest

@JvmInline value class KeyboardVisible(val value: Boolean)

context(NamedCoroutineScope<AppScope>) @Provide fun keyboardVisible(
  accessibilityEvents: Flow<AccessibilityEvent>,
  keyboardHeightProvider: @KeyboardHeightProvider () -> Int?
): @Scoped<AppScope> Flow<KeyboardVisible> = accessibilityEvents
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
  .map { KeyboardVisible(it) }
  .distinctUntilChanged()
  .stateIn(inject(), SharingStarted.WhileSubscribed(1000), KeyboardVisible(false))

@Provide val keyboardVisibilityAccessibilityConfig: AccessibilityConfig
  get() = AccessibilityConfig(
    eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
  )

@Tag private annotation class KeyboardHeightProvider

@Provide fun keyboardHeightProvider(
  inputMethodManager: @SystemService InputMethodManager
): @KeyboardHeightProvider () -> Int? = {
  catch {
    val method = inputMethodManager.javaClass.getMethod("getInputMethodWindowVisibleHeight")
    method.invoke(inputMethodManager) as Int
  }.getOrNull()
}
