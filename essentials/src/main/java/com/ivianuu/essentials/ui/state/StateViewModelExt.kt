package com.ivianuu.essentials.ui.state

import android.arch.lifecycle.ViewModelProvider
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.essentials.util.lifecycleAwareLazy
import kotlin.reflect.KClass


inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.viewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy {
    viewModelProvider(factoryProvider()).get(keyProvider(), clazz.java).setupViewModel(this)
}

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.activityViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy {
    requireActivity().viewModelProvider(factoryProvider()).get(keyProvider(), clazz.java)
        .setupViewModel(this)
}

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.parentViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy {
    requireParentFragment().viewModelProvider(factoryProvider()).get(keyProvider(), clazz.java)
        .setupViewModel(this)
}

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.targetViewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy {
    requireTargetFragment().viewModelProvider(factoryProvider()).get(keyProvider(), clazz.java)
        .setupViewModel(this)
}

@PublishedApi
internal inline fun <reified T : StateViewModel<S>, reified S> T.setupViewModel(BaseFragment: BaseFragment) =
    apply { subscribe(BaseFragment) { BaseFragment.postInvalidate() } }