/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ivianuu.essentials.AppContext
import com.ivianuu.injekt.Provide

@SuppressLint("ViewConstructor")
@Provide
class OverlayComposeView(
  context: AppContext,
  private val content: @Composable () -> Unit,
) : AbstractComposeView(context),
  LifecycleOwner,
  SavedStateRegistryOwner,
  ViewModelStoreOwner {
  private val _lifecycle = LifecycleRegistry(this)
  override val lifecycle: Lifecycle
    get() = _lifecycle
  private val savedStateRegistryController = SavedStateRegistryController.create(this)
  override val viewModelStore = ViewModelStore()

  init {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
    setViewTreeLifecycleOwner(this)
    savedStateRegistryController.performRestore(null)
    setViewTreeSavedStateRegistryOwner(this)
    setViewTreeViewModelStoreOwner(this)
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

  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  fun dispose() {
    _lifecycle.currentState = Lifecycle.State.DESTROYED
    viewModelStore.clear()
  }
}
