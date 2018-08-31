package com.ivianuu.essentials.ui.state

import android.arch.lifecycle.ViewModelProvider
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.essentials.util.lifecycleAwareLazy
import kotlin.reflect.KClass

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.viewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { viewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.activityViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireActivity().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindActivityViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { activityViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.parentViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireParentFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindParentViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { parentViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.targetViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireTargetFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindTargetViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { targetViewModel(clazz, factoryProvider(), keyProvider()) }

@PublishedApi
internal inline fun <reified T : StateViewModel<S>, reified S> T.setupViewModel(stateFragment: StateFragment) =
    apply {
        subscribe(stateFragment) { stateFragment.postInvalidate() }
    }