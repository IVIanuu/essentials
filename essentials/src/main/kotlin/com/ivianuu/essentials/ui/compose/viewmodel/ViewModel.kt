package com.ivianuu.essentials.ui.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.memo
import com.ivianuu.compose.onDispose
import kotlin.reflect.KClass

val ComponentComposition.viewModelStore: ViewModelStore
    get() {
        val viewModelStore = this.memo { ViewModelStore() }
        this.onDispose { viewModelStore.clear() } // todo clear only once
        return viewModelStore
    }

inline fun <reified T : ViewModel> ComponentComposition.viewModel(noinline factory: () -> T) =
    viewModel(T::class, factory)

fun <T : ViewModel> ComponentComposition.viewModel(type: KClass<T>, factory: () -> T): T {
    val provider = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return factory() as T
        }
    })

    return provider.get(type.java)
}