package com.ivianuu.essentials.ui.compose.mvrx

import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.defaultViewModelKey
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.get

inline fun <reified T : MvRxViewModel<*>> mvRxViewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    key: String = T::class.defaultViewModelKey,
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = effectOf<T> {
    val component = +ambient(ComponentAmbient)
    val createViewModel: () -> T = { component.get(name, parameters) }
    return@effectOf +mvRxViewModel<T>(
        from = from,
        key = key,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = createViewModel() as T
        }
    )
}