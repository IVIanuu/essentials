package com.ivianuu.essentials.ui.compose.mvrx

import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.coroutines.flow
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.viewmodel.viewModel
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.defaultViewModelKey
import kotlinx.coroutines.flow.drop

inline fun <reified T : MvRxViewModel<*>> mvRxViewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory(),
    key: String = T::class.defaultViewModelKey
) = effectOf<T> {
    val viewModel = +viewModel<T>(from, factory, key)

    // recompose on changes
    +flow(viewModel.flow.drop(1))

    return@effectOf viewModel
}