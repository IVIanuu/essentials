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

import android.annotation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import androidx.savedstate.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

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
  private val savedStateRegistryController = SavedStateRegistryController.create(this)
  private val viewModelStore = ViewModelStore()

  init {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
    ViewTreeLifecycleOwner.set(this, this)
    ViewTreeSavedStateRegistryOwner.set(this, this)
    savedStateRegistryController.performRestore(null)
    ViewTreeViewModelStoreOwner.set(this, this)
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

  override fun getLifecycle(): Lifecycle = _lifecycle

  override fun getViewModelStore(): ViewModelStore = viewModelStore

  override fun getSavedStateRegistry(): SavedStateRegistry =
    savedStateRegistryController.savedStateRegistry

  fun dispose() {
    _lifecycle.currentState = Lifecycle.State.DESTROYED
    viewModelStore.clear()
  }
}
