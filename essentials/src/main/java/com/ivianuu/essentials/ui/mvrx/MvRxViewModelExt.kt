package com.ivianuu.essentials.ui.mvrx

import androidx.lifecycle.ViewModelProvider
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.defaultViewModelFactory
import com.ivianuu.essentials.util.ext.defaultViewModelKey
import com.ivianuu.essentials.util.ext.requireParentFragment
import com.ivianuu.essentials.util.ext.requireTargetFragment
import com.ivianuu.essentials.util.ext.viewModelProvider
import com.ivianuu.essentials.util.lifecycle.lifecycleAwareLazy
import kotlin.reflect.KClass

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.viewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.bindViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { viewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.activityViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireActivity().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.bindActivityViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { activityViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.parentViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireParentFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.bindParentViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { parentViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.targetViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireTargetFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : MvRxViewModel<S>, reified S : MvRxState> BaseFragment.bindTargetViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { targetViewModel(clazz, factoryProvider(), keyProvider()) }

@PublishedApi
internal inline fun <reified T : MvRxViewModel<S>, reified S> T.setupViewModel(baseFragment: BaseFragment) =
    apply { subscribe(baseFragment) { baseFragment.postInvalidate() } }