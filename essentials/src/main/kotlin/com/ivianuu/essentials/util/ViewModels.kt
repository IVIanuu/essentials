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
): T = getViewModel(T::class, factory, key)

fun <T : ViewModel> ViewModelStoreOwner.getViewModel(
    type: KClass<T>,
    factory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory(),
    key: String = type.defaultViewModelKey
): T = viewModelProvider(factory).get(key, type.java)

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    noinline keyProvider: () -> String = { T::class.defaultViewModelKey },
    noinline factoryProvider: () -> ViewModelProvider.Factory = { ViewModelProvider.NewInstanceFactory() }
): Lazy<ViewModel> = viewModel(T::class, keyProvider, factoryProvider)

fun <T : ViewModel> ViewModelStoreOwner.viewModel(
    type: KClass<T>,
    keyProvider: () -> String = { type.defaultViewModelKey },
    factoryProvider: () -> ViewModelProvider.Factory = { ViewModelProvider.NewInstanceFactory() }
): Lazy<ViewModel> =
    lazy(LazyThreadSafetyMode.NONE) { getViewModel(type, factoryProvider(), keyProvider()) }
