package com.ivianuu.essentials.ui.compose.mvrx

import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.defaultViewModelKey
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf
import kotlin.reflect.KClass

inline fun <reified T : MvRxViewModel<*>> mvRxViewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    key: String = +memo { T::class.defaultViewModelKey },
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = mvRxViewModel(typeOf<T>(), from, key, name, parameters)

fun <T : MvRxViewModel<*>> mvRxViewModel(
    type: Type<T>,
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    key: String = +memo { (type.raw as KClass<T>).defaultViewModelKey },
    name: Any? = null,
    parameters: ParametersDefinition? = null
) = effectOf<T> {
    val component = +ambient(ComponentAmbient)

    val factory = +memo<ViewModelProvider.Factory> {
        InjektMvRxViewModelFactory(component, type, name, parameters)
    }

    return@effectOf +mvRxViewModel(
        type = type.raw as KClass<T>,
        from = from,
        key = key,
        factory = factory
    )
}

private class InjektMvRxViewModelFactory<T : MvRxViewModel<*>>(
    private val component: Component,
    private val type: Type<T>,
    private val name: Any?,
    private val parameters: ParametersDefinition?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        component.get(type, name, parameters) as T
}