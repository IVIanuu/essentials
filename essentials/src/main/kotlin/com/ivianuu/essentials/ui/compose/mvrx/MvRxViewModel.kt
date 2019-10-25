package com.ivianuu.essentials.ui.compose.mvrx

import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.coroutines.collect
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.viewmodel.viewModel
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.defaultViewModelKey
import kotlin.reflect.KClass

inline fun <reified T : MvRxViewModel<*>> mvRxViewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = +memo { ViewModelProvider.NewInstanceFactory() },
    key: String = +memo { T::class.defaultViewModelKey }
) = mvRxViewModel(T::class, from, factory, key)

fun <T : MvRxViewModel<*>> mvRxViewModel(
    type: KClass<T>,
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    factory: ViewModelProvider.Factory = +memo { ViewModelProvider.NewInstanceFactory() },
    key: String = +memo { type.defaultViewModelKey }
) = effectOf<T> {
    val viewModel = +viewModel(type, from, factory, key)
    // recompose on changes
    +collect(viewModel.flow)
    return@effectOf viewModel
}