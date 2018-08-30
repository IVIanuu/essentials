package com.ivianuu.essentials.ui.state

import android.arch.lifecycle.ViewModelProvider
import com.ivianuu.essentials.util.ext.requireParentFragment
import com.ivianuu.essentials.util.ext.requireTargetFragment
import com.ivianuu.essentials.util.ext.viewModel
import com.ivianuu.essentials.util.lifecycleAwareLazy

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.stateViewModel() =
    viewModel<T>().setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.stateViewModel(
    factory: ViewModelProvider.Factory
) = viewModel<T>(factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.stateViewModel(
    key: String
) = viewModel<T>(key).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.stateViewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = viewModel<T>(key, factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindStateViewModel() =
    lifecycleAwareLazy { stateViewModel<T, S>() }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindStateViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = lifecycleAwareLazy { stateViewModel<T, S>(factory()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindStateViewModel(
    key: String
) = lifecycleAwareLazy { stateViewModel<T, S>(key) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.activityStateViewModel() =
    requireActivity().viewModel<T>().setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.activityStateViewModel(
    factory: ViewModelProvider.Factory
) = requireActivity().viewModel<T>(factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.activityStateViewModel(
    key: String
) = requireActivity().viewModel<T>(key).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.activityStateViewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = requireActivity().viewModel<T>(key, factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindActivityStateViewModel() =
    lifecycleAwareLazy { activityStateViewModel<T, S>() }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindActivityStateViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = lifecycleAwareLazy { activityStateViewModel<T, S>(factory()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindActivityStateViewModel(
    key: String
) = lifecycleAwareLazy { activityStateViewModel<T, S>(key) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.parentStateViewModel() =
    requireParentFragment().viewModel<T>().setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.parentStateViewModel(
    factory: ViewModelProvider.Factory
) = requireParentFragment().viewModel<T>(factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.parentStateViewModel(
    key: String
) = requireParentFragment().viewModel<T>().setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.parentStateViewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = requireParentFragment().viewModel<T>(key, factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindParentStateViewModel() =
    lifecycleAwareLazy { parentStateViewModel<T, S>() }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindParentStateViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = lifecycleAwareLazy { parentStateViewModel<T, S>(factory()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindParentStateViewModel(
    key: String
) = lifecycleAwareLazy { parentStateViewModel<T, S>(key) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.targetStateViewModel() =
    requireTargetFragment().viewModel<T>().setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.targetStateViewModel(
    factory: ViewModelProvider.Factory
) = requireTargetFragment().viewModel<T>(factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.targetStateViewModel(
    key: String
) = requireTargetFragment().viewModel<T>(key).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.targetStateViewModel(
    key: String,
    factory: ViewModelProvider.Factory
) = requireTargetFragment().viewModel<T>(key, factory).setupViewModel(this)

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindTargetStateViewModel() =
    lifecycleAwareLazy { targetStateViewModel<T, S>() }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindTargetStateViewModel(
    crossinline factory: () -> ViewModelProvider.Factory
) = lifecycleAwareLazy { targetStateViewModel<T, S>(factory()) }

inline fun <reified T : StateViewModel<S>, reified S : Any> StateFragment.bindTargetStateViewModel(
    key: String
) = lifecycleAwareLazy { targetStateViewModel<T, S>(key) }

@PublishedApi
internal inline fun <reified T : StateViewModel<S>, reified S> T.setupViewModel(stateFragment: StateFragment) =
    apply {
        subscribe(stateFragment) { stateFragment.postInvalidate() }
    }