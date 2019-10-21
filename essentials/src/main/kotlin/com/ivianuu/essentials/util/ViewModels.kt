package com.ivianuu.essentials.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

// todo remove

val KClass<out ViewModel>.defaultViewModelKey
    get() = "androidx.lifecycle.ViewModelProvider.DefaultKey:" + java.canonicalName

fun ViewModelStoreOwner.viewModelProvider(
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory()
): ViewModelProvider = ViewModelProvider(this, factory)

inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory(),
    key: String = T::class.defaultViewModelKey
): T = viewModelProvider(factory).get(key, T::class.java)

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    crossinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    crossinline factoryProvider: () -> ViewModelProvider.Factory = { ViewModelProvider.NewInstanceFactory() }
): Lazy<ViewModel> =
    lazy(LazyThreadSafetyMode.NONE) { getViewModel<T>(factoryProvider(), keyProvider()) }
