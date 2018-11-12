package com.ivianuu.essentials.ui.mvrx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.androidktx.fragment.app.requireParentFragment
import com.ivianuu.androidktx.fragment.app.requireTargetFragment
import com.ivianuu.androidktx.lifecycle.defaultViewModelKey
import com.ivianuu.androidktx.lifecycle.viewModelProvider
import com.ivianuu.essentials.util.ViewModelFactoryHolder
import kotlin.reflect.KClass

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.viewModel(
    clazz: KClass<VM>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = VM::class.defaultViewModelKey
) = viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = viewModelLazy { viewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.existingViewModel(
    clazz: KClass<VM>,
    key: String = VM::class.defaultViewModelKey
) = viewModel(clazz, ExistingViewModelFactory, key)

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindExistingViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) = viewModelLazy { existingViewModel(clazz, keyProvider()) }

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.activityViewModel(
    clazz: KClass<VM>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = VM::class.defaultViewModelKey
): VM {
    val activity = when {
        this is FragmentActivity -> this
        this is Fragment -> requireActivity()
        else -> throw IllegalArgumentException("must be an activity or an fragment")
    }

    return activity.viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)
}

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindActivityViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = viewModelLazy { activityViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.existingActivityViewModel(
    clazz: KClass<VM>,
    key: String = VM::class.defaultViewModelKey
) = activityViewModel(clazz, ExistingViewModelFactory, key)

inline fun <T : MvRxView, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindExistingActivityViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) = viewModelLazy { existingActivityViewModel(clazz, keyProvider()) }

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.parentViewModel(
    clazz: KClass<VM>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = VM::class.defaultViewModelKey
) where T : MvRxView, T : Fragment =
    requireParentFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindParentViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) where T : MvRxView, T : Fragment =
    viewModelLazy { parentViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.existingParentViewModel(
    clazz: KClass<VM>,
    key: String = VM::class.defaultViewModelKey
) where T : MvRxView, T : Fragment = parentViewModel(clazz, ExistingViewModelFactory, key)

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindExistingParentViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) where T : MvRxView, T : Fragment = viewModelLazy { existingParentViewModel(clazz, keyProvider()) }

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.targetViewModel(
    clazz: KClass<VM>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = VM::class.defaultViewModelKey
) where T : MvRxView, T : Fragment =
    requireTargetFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindTargetViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) where T : MvRxView, T : Fragment =
    viewModelLazy { targetViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.existingTargetViewModel(
    clazz: KClass<VM>,
    key: String = VM::class.defaultViewModelKey
) where T : MvRxView, T : Fragment = targetViewModel(clazz, ExistingViewModelFactory, key)

inline fun <T, reified VM : MvRxViewModel<S>, reified S : MvRxState> T.bindExistingTargetViewModel(
    clazz: KClass<VM>,
    crossinline keyProvider: () -> String = { VM::class.defaultViewModelKey }
) where T : MvRxView, T : Fragment =
    viewModelLazy { existingTargetViewModel(clazz, keyProvider()) }

@PublishedApi
internal fun <VM : MvRxViewModel<S>, S> VM.setupViewModel(view: MvRxView) =
    apply { subscribe(view) { view.postInvalidate() } }

@PublishedApi
internal fun Any.defaultViewModelFactory() = if (this is ViewModelFactoryHolder) {
    viewModelFactory
} else {
    ViewModelProvider.NewInstanceFactory()
}

@PublishedApi
internal fun <T : MvRxView, V> T.viewModelLazy(initializer: () -> V) =
    lifecycleAwareLazy(Lifecycle.Event.ON_CREATE, initializer)

@PublishedApi
internal object ExistingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        throw IllegalStateException("viewmodel $modelClass does not exist.")
    }
}