/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import android.annotation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import androidx.savedstate.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*

@SuppressLint("ViewConstructor")
@Provide
class OverlayComposeView(
  appContext: AppContext,
  private val content: @Composable () -> Unit,
) : AbstractComposeView(appContext),
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
