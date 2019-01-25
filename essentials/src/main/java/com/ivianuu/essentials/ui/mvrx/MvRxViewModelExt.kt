package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.injekt.get
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> MvRxView.viewModel(
    noinline from: () -> ViewModelStoreOwner = { this },
    noinline key: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM = { get() }
): Lazy<VM> = viewModelLazy { getViewModel(from(), key(), factory) }

inline fun <reified VM : MvRxViewModel<*>> MvRxView.getViewModel(
    from: ViewModelStoreOwner = this,
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM = { get() }
): VM = viewModelProvider(from, factory).get(key, VM::class.java)
    .setupViewModel(this)

inline fun <reified VM : MvRxViewModel<*>> MvRxView.existingViewModel(
    noinline from: () -> ViewModelStoreOwner = { this },
    noinline key: () -> String = { VM::class.defaultViewModelKey }
): Lazy<VM> = viewModel(from, key, ExistingViewModelFactory())

inline fun <reified VM : MvRxViewModel<*>> MvRxView.getExistingViewModel(
    from: ViewModelStoreOwner = this,
    key: String = VM::class.defaultViewModelKey
): VM = getViewModel(from, key, ExistingViewModelFactory())

@PublishedApi
internal fun <VM : MvRxViewModel<*>> VM.setupViewModel(view: MvRxView): VM =
    apply { subscribe(view) { view.postInvalidate() } }

@PublishedApi
internal fun <V> MvRxView.viewModelLazy(initializer: () -> V): Lazy<V> =
    lifecycleAwareLazy(Lifecycle.Event.ON_START, initializer)

@PublishedApi
internal fun <VM : MvRxViewModel<*>> viewModelProvider(
    from: ViewModelStoreOwner,
    factory: () -> VM
): ViewModelProvider = ViewModelProvider(from, MvRxViewModelFactory(factory))

@PublishedApi
internal class ExistingViewModelFactory<VM : MvRxViewModel<*>> : () -> VM {
    override fun invoke(): VM {
        error("view model does not exist.")
    }
}

@PublishedApi
internal class MvRxViewModelFactory<VM : MvRxViewModel<*>>(
    private val factory: () -> VM
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = factory() as T
}