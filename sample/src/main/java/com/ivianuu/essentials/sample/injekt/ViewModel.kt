package com.ivianuu.essentials.sample.injekt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.injekt.*

inline fun <reified T : ViewModel> Module.viewModel(
    name: String? = null,
    override: Boolean = false,
    noinline definition: BeanDefinition<T>
) = factory(T::class, name, override, definition)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified VM : ViewModel> T.viewModel(
    noinline key: (() -> String)? = null,
    noinline name: (() -> String)? = null,
    noinline parameters: ParamsDefinition? = null
) where T : ViewModelStoreOwner, T : ComponentHolder = lazy {
    getViewModel<T, VM>(key?.invoke(), name?.invoke(), parameters)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified VM : ViewModel> T.getViewModel(
    key: String? = null,
    name: String? = null,
    noinline parameters: ParamsDefinition? = null
): VM where T : ViewModelStoreOwner, T : ComponentHolder {
    val provider = ViewModelProvider(
        this,
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>) =
                inject<VM>(name, parameters) as T
        }
    )

    return if (key != null) {
        provider.get(key, VM::class.java)
    } else {
        provider.get(VM::class.java)
    }
}