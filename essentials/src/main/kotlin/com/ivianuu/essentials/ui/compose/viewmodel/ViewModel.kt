package com.ivianuu.essentials.ui.compose.viewmodel

import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.defaultViewModelKey
import com.ivianuu.essentials.util.getViewModel

inline fun <reified T : ViewModel> viewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory(),
    key: String = T::class.defaultViewModelKey
) = effectOf<T> {
    from.getViewModel(factory, key)
}