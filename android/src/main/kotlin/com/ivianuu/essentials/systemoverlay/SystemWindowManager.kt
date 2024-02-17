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

import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalView
import arrow.core.Either
import arrow.fx.coroutines.bracketCase
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.LocalScope
import com.ivianuu.essentials.Scope
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.accessibility.AccessibilityWindowManager
import com.ivianuu.essentials.coroutines.CoroutineContexts
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

data object SystemWindowScope

class SystemWindowState(
  width: Int = WindowManager.LayoutParams.MATCH_PARENT,
  height: Int = WindowManager.LayoutParams.MATCH_PARENT,
  x: Int = 0,
  y: Int = 0,
  interceptor: (WindowManager.LayoutParams) -> Unit = {}
) {
  var width by mutableStateOf(width)
  var height by mutableStateOf(height)
  var x by mutableStateOf(x)
  var y by mutableStateOf(y)
  var interceptor by mutableStateOf(interceptor)
}

fun Modifier.systemWindowTrigger() = composed {
  val ownerView = LocalView.current
  val triggerView = remember { TriggerView(ownerView) }
  val systemWindowManager = LocalSystemWindowManager.current
  val layoutParams = remember {
    WindowManager.LayoutParams().apply {
      gravity = Gravity.LEFT or Gravity.TOP

      type =
        if (systemWindowManager.canUseAccessibility) WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
      format = PixelFormat.TRANSLUCENT

      flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
          WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
          WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
          WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
          WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
    }
  }

  DisposableEffect(true) {
    onDispose {
      Either.catch {
        systemWindowManager.windowManager.removeViewImmediate(triggerView)
      }
    }
  }

  onGloballyPositioned { coords ->
    var dirty = false

    layoutParams.apply {
      if (width != coords.size.width) {
        width = coords.size.width
        dirty = true
      }
      if (height != coords.size.height) {
        height = coords.size.height
        dirty = true
      }

      val ownerLoc = intArrayOf(0, 0)
      ownerView.getLocationOnScreen(ownerLoc)
      val positionInWindow = coords.positionInWindow()

      val newX = positionInWindow.x.roundToInt() + ownerLoc[0]
      if (x != newX) {
        x = newX
        dirty = true
      }
      val newY = positionInWindow.y.roundToInt() + ownerLoc[1]
      if (y != newY) {
        y = newY
        dirty = true
      }

      if (dirty)
        systemWindowManager.windowManager.addOrUpdateView(triggerView, layoutParams)
    }
  }
}

@Provide class SystemWindowManager(
  private val appContext: AppContext,
  accessibilityWindowManager: AccessibilityWindowManager? = null,
  private val coroutineContexts: CoroutineContexts,
  private val systemWindowScopeFactory: () -> Scope<SystemWindowScope>,
  windowManager: @SystemService WindowManager
) {
  internal val canUseAccessibility = accessibilityWindowManager != null
  internal val windowManager = accessibilityWindowManager ?: windowManager

  suspend fun attachSystemWindow(
    state: SystemWindowState = SystemWindowState(),
    content: @Composable () -> Unit
  ): Nothing = withContext(coroutineContexts.main) {
    bracketCase(
      acquire = {
        lateinit var contentView: View
        contentView = OverlayComposeView(appContext) {
          val scope = remember(systemWindowScopeFactory)
          DisposableEffect(true) {
            onDispose { scope.dispose() }
          }
          CompositionLocalProvider(LocalScope provides scope) {
            Window(contentView, state, content)
          }
        }
        contentView
      },
      use = { contentView ->
        windowManager.addView(
          contentView,
          WindowManager.LayoutParams().apply {
            this.width = WindowManager.LayoutParams.WRAP_CONTENT
            this.height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.LEFT or Gravity.TOP

            type = if (canUseAccessibility) WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY

            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED

            format = PixelFormat.TRANSLUCENT

            state.interceptor(this)
          }
        )
        awaitCancellation()
      },
      { contentView, _ ->
        Either.catch { windowManager.removeViewImmediate(contentView) }
        contentView.dispose()
      }
    )
  }

  @Composable private fun Window(
    contentView: View,
    state: SystemWindowState,
    content: @Composable () -> Unit
  ) {
    DisposableEffect(
      state.width,
      state.height,
      state.x,
      state.y,
      state.interceptor
    ) {
      windowManager.addOrUpdateView(
        contentView,
        (contentView.layoutParams as WindowManager.LayoutParams).apply {
          this.width = state.width
          this.height = state.height
          this.x = state.x
          this.y = state.y
          state.interceptor(this)
        }
      )
      onDispose {
      }
    }

    CompositionLocalProvider(
      LocalSystemWindowManager provides this,
      content = content
    )
  }
}

internal val LocalSystemWindowManager = staticCompositionLocalOf<SystemWindowManager> {
  throw IllegalStateException("No system window manager provided")
}

private fun WindowManager.addOrUpdateView(view: View, layoutParams: WindowManager.LayoutParams) {
  if (view.isAttachedToWindow) {
    updateViewLayout(view, layoutParams)
  } else {
    Either.catch { removeViewImmediate(view) }
    addView(view, layoutParams)
  }
}
