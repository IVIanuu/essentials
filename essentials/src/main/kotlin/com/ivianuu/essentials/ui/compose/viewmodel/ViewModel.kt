package com.ivianuu.essentials.ui.compose.viewmodel

import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.util.defaultViewModelKey
import com.ivianuu.essentials.util.getViewModel
import kotlin.reflect.KClass

inline fun <reified T : ViewModel> viewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = +memo { ViewModelProvider.NewInstanceFactory() },
    key: String = +memo { T::class.defaultViewModelKey }
) = viewModel(T::class, from, factory, key)

fun <T : ViewModel> viewModel(
    type: KClass<T>,
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = +memo { ViewModelProvider.NewInstanceFactory() },
    key: String = +memo { type.defaultViewModelKey }
) = effectOf<T> {
    +memo { from.getViewModel(type, factory, key) }
}