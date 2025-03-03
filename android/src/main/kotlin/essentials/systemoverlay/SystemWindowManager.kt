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

package essentials.systemoverlay

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.*
import android.view.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.core.content.*
import arrow.fx.coroutines.*
import essentials.*
import essentials.coroutines.*
import injekt.*
import kotlinx.coroutines.*
import kotlin.math.*

data object SystemWindowScope

class SystemWindowState(
  width: Int = WindowManager.LayoutParams.MATCH_PARENT,
  height: Int = WindowManager.LayoutParams.MATCH_PARENT,
  x: Int = 0,
  y: Int = 0,
  interceptor: (WindowManager.LayoutParams) -> Unit = {}
) {
  var width by mutableIntStateOf(width)
  var height by mutableIntStateOf(height)
  var x by mutableIntStateOf(x)
  var y by mutableIntStateOf(y)
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
        if (systemWindowManager.accessibilityAvailable)
          WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
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
      catch {
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

@Stable @Provide class SystemWindowManager(
  private val context: Context,
  private val systemWindowScopeFactory: () -> Scope<SystemWindowScope>,
  val windowManager: @SystemService WindowManager
) {
  val accessibilityAvailable: Boolean
    get() = context is AccessibilityService

  @Composable fun SystemWindow(
    state: SystemWindowState = SystemWindowState(),
    content: @Composable () -> Unit
  ) {
    val contentView = remember {
      lateinit var contentView: OverlayComposeView
      contentView = OverlayComposeView(context) {
        val scope = remember(systemWindowScopeFactory)
        DisposableEffect(true) {
          onDispose { scope.dispose() }
        }
        CompositionLocalProvider(LocalScope provides scope) {
          Window(contentView, state, content)
        }
      }
      contentView
    }

    DisposableEffect(contentView) {
      windowManager.addView(
        contentView,
        WindowManager.LayoutParams().apply {
          this.width = WindowManager.LayoutParams.WRAP_CONTENT
          this.height = WindowManager.LayoutParams.WRAP_CONTENT
          gravity = Gravity.LEFT or Gravity.TOP

          type = if (accessibilityAvailable) WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
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

      onDispose {
        catch { windowManager.removeViewImmediate(contentView) }
        contentView.dispose()
      }
    }
  }

  @Composable private fun Window(
    contentView: OverlayComposeView,
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
    catch { removeViewImmediate(view) }
    addView(view, layoutParams)
  }
}
