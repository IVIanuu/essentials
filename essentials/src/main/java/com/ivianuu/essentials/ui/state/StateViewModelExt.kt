package com.ivianuu.essentials.ui.state

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.base.BaseFragment
import com.ivianuu.essentials.util.ext.requireParentFragment
import com.ivianuu.essentials.util.ext.requireTargetFragment
import com.ivianuu.essentials.util.lifecycle.lifecycleAwareLazy
import com.ivianuu.essentials.util.viewmodel.ViewModelFactoryHolder
import kotlin.reflect.KClass

inline fun <reified T : StateViewModel<S>, reified S : Any> BaseFragment.viewModel(
    clazz: KClass<T>,
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { defaultViewModelFactory() }
) = lifecycleAwareLazy {
    viewModelProvider(factoryProvider()).get(keyProvider(), clazz.java)
        .setupViewModel(this)
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
internal inline fun <reified T : StateViewModel<S>, reified S> T.setupViewModel(baseFragment: BaseFragment) =
    apply { subscribe(baseFragment) { baseFragment.postInvalidate() } }

@PublishedApi
internal fun ViewModelStoreOwner.viewModelProvider(
    factory: ViewModelProvider.Factory = defaultViewModelFactory()
) = ViewModelProvider(this, factory)

@PublishedApi
internal val KClass<*>.defaultViewModelKey
    get() = "android.arch.lifecycle.ViewModelProvider.DefaultKey:" + java.canonicalName


@PublishedApi
internal fun ViewModelStoreOwner.defaultViewModelFactory() =
    if (this is ViewModelFactoryHolder) {
        viewModelFactory
    } else {
        ViewModelProvider.NewInstanceFactory()
    }