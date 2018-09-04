package com.ivianuu.essentials.ui.state

import androidx.lifecycle.ViewModelProvider
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.essentials.util.lifecycleAwareLazy
import kotlin.reflect.KClass

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.viewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.bindViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { viewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.activityViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireActivity().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.bindActivityViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { activityViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.parentViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireParentFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.bindParentViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { parentViewModel(clazz, factoryProvider(), keyProvider()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.targetViewModel(
    clazz: KClass<T>,
    factory: ViewModelProvider.Factory = defaultViewModelFactory(),
    key: String = T::class.defaultViewModelKey
) = requireTargetFragment().viewModelProvider(factory).get(key, clazz.java).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.bindTargetViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy { targetViewModel(clazz, factoryProvider(), keyProvider()) }

@PublishedApi
internal inline fun <reified T : StateViewModel<S>, reified S> T.setupViewModel(BaseFragment: BaseFragment) =
    apply {
        subscribe(BaseFragment) { BaseFragment.postInvalidate() }
    }