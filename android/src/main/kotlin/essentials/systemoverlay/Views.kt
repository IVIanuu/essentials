/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:SuppressLint("ViewConstructor")

package essentials.systemoverlay

import android.annotation.*
import android.content.*
import android.view.*
import android.widget.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import androidx.savedstate.*
import injekt.*

@Stable @Provide class SystemWindowComposeView(
  context: Context,
  private val content: @Composable () -> Unit,
) : AbstractComposeView(context),
  LifecycleOwner,
  SavedStateRegistryOwner {
  private val _lifecycle = LifecycleRegistry(this)
  override val lifecycle: Lifecycle get() = _lifecycle
  private val savedStateRegistryController = SavedStateRegistryController.create(this)
  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  init {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
    setViewTreeLifecycleOwner(this)
    savedStateRegistryController.performRestore(null)
    setViewTreeSavedStateRegistryOwner(this)
    _lifecycle.currentState = Lifecycle.State.CREATED
  }

  @Composable override fun Content() {
    content()
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    _lifecycle.currentState = Lifecycle.State.RESUMED
  }

  override fun onDetachedFromWindow() {
    _lifecycle.currentState = Lifecycle.State.CREATED
    super.onDetachedFromWindow()
  }
}

@Stable class TriggerView(private val delegate: View) : FrameLayout(delegate.context) {
  var useDownTouchOffset = true

  private val thisDownLocation = intArrayOf(0, 0)
  private val thisLocation = intArrayOf(0, 0)
  private val delegateLocation = intArrayOf(0, 0)

  @SuppressLint("ClickableViewAccessibility")
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    getLocationOnScreen(thisLocation)
    delegate.getLocationOnScreen(delegateLocation)

    if (useDownTouchOffset) {
      if (ev.action == MotionEvent.ACTION_DOWN) {
        // capture down location
        thisDownLocation[0] = thisLocation[0]
        thisDownLocation[1] = thisLocation[1]
      } else {
        // translate to down location view
        ev.offsetLocation(
          (thisLocation[0] - thisDownLocation[0]).toFloat(),
          (thisLocation[1] - thisDownLocation[1]).toFloat()
        )
      }

      // translate to delegate view
      ev.offsetLocation(
        (thisDownLocation[0] - delegateLocation[0]).toFloat(),
        (thisDownLocation[1] - delegateLocation[1]).toFloat()
      )
    } else {
      // translate to delegate view
      ev.offsetLocation(
        (thisLocation[0] - delegateLocation[0]).toFloat(),
        (thisLocation[1] - delegateLocation[1]).toFloat()
      )
    }

    return delegate.dispatchTouchEvent(ev)
  }
}
