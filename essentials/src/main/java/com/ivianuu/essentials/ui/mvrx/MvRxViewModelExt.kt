package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.androidktx.lifecycle.defaultViewModelKey

inline fun <reified VM : MvRxViewModel<*>> MvRxView.viewModel(
    key: String = VM::class.defaultViewModelKey,
    noinline factory: () -> VM
) = viewModelProvider(factory).get(key, VM::class.java).setupViewModel(this)

inline fun <reified VM : MvRxViewModel<*>> MvRxView.bindViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    noinline factory: () -> VM
) = viewModelLazy { viewModel(keyProvider(), factory) }

inline fun <reified VM : MvRxViewModel<*>> MvRxView.existingViewModel(
    key: String = VM::class.defaultViewModelKey
) = viewModel<VM>(key, ExistingViewModelFactory())

inline fun <reified VM : MvRxViewModel<*>> MvRxView.bindExistingViewModel(
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) = viewModelLazy { existingViewModel<VM>(keyProvider()) }

@PublishedApi
internal fun <VM : MvRxViewModel<*>> VM.setupViewModel(view: MvRxView) =
    apply { subscribe(view) { view.postInvalidate() } }

@PublishedApi
internal fun <V> MvRxView.viewModelLazy(initializer: () -> V) =
    lifecycleAwareLazy(Lifecycle.Event.ON_CREATE, initializer)

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