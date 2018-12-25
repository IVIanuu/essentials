package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.kommon.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> MvRxView.viewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) = viewModelLazy { getViewModel(keyProvider(), factory) }

inline fun <reified VM : MvRxViewModel<*>> MvRxView.existingViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) = viewModelLazy { getExistingViewModel<VM>(keyProvider()) }

inline fun <reified VM : MvRxViewModel<*>> MvRxView.getViewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = viewModelProvider(factory).get(key, VM::class.java).setupViewModel(this)

inline fun <reified VM : MvRxViewModel<*>> MvRxView.getExistingViewModel(
    key: String = VM::class.defaultViewModelKey
) = viewModelProvider<VM>(ExistingViewModelFactory())
    .get(key, VM::class.java)
    .setupViewModel(this)

@PublishedApi
internal fun <VM : MvRxViewModel<*>> VM.setupViewModel(view: MvRxView) =
    apply { subscribe(view) { view.postInvalidate() } }

@PublishedApi
internal fun <V> MvRxView.viewModelLazy(initializer: () -> V) =
    lifecycleAwareLazy(Lifecycle.Event.ON_START, initializer)

@PublishedApi
internal fun <VM : MvRxViewModel<*>> ViewModelStoreOwner.viewModelProvider(
    factory: () -> VM
) = ViewModelProvider(this, MvRxViewModelFactory(factory))

@PublishedApi
internal class ExistingViewModelFactory<VM : MvRxViewModel<*>> : () -> VM {
    override fun invoke(): VM {
        throw IllegalStateException("viewmodel does not exist.")
    }
}

@PublishedApi
internal class MvRxViewModelFactory<VM : MvRxViewModel<*>>(
    private val factory: () -> VM
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = factory() as T
}