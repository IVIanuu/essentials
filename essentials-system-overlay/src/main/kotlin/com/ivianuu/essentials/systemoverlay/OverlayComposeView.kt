package com.ivianuu.essentials.systemoverlay

import android.annotation.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import androidx.lifecycle.*
import androidx.savedstate.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

@SuppressLint("ViewConstructor")
@Given
class OverlayComposeView(
  @Given appContext: AppContext,
  @Given private val content: @Composable () -> Unit,
) : AbstractComposeView(appContext),
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
