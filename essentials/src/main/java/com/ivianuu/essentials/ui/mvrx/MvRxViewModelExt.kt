package com.ivianuu.essentials.ui.mvrx

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.ivianuu.essentials.util.ext.defaultViewModelFactory
import com.ivianuu.essentials.util.ext.defaultViewModelKey
import com.ivianuu.essentials.util.ext.requireParentFragment
import com.ivianuu.essentials.util.ext.requireTargetFragment
import com.ivianuu.essentials.util.ext.viewModelProvider
import com.ivianuu.essentials.util.lifecycle.lifecycleAwareLazy
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
) = lifecycleAwareLazy { viewModel(clazz, factoryProvider(), keyProvider()) }

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
) = lifecycleAwareLazy { activityViewModel(clazz, factoryProvider(), keyProvider()) }

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
    lifecycleAwareLazy { parentViewModel(clazz, factoryProvider(), keyProvider()) }

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
    lifecycleAwareLazy { targetViewModel(clazz, factoryProvider(), keyProvider()) }

@PublishedApi
internal inline fun <reified VM : MvRxViewModel<S>, reified S> VM.setupViewModel(view: MvRxView) =
    apply { subscribe(view) { view.postInvalidate() } }