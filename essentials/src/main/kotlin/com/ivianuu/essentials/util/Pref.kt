package com.ivianuu.essentials.util

import com.ivianuu.epoxyprefs.AbstractPreferenceModel
import com.ivianuu.epoxyprefs.dependency
import com.ivianuu.kprefs.Pref
import com.ivianuu.kprefs.common.PrefValueHolder

fun <T : Any> AbstractPreferenceModel.Builder<T>.fromPref(pref: Pref<T>) {
    key(pref.key)
    defaultValue(pref.defaultValue)
}

fun <T, S : Any> AbstractPreferenceModel.Builder<S>.fromEnumPref(pref: Pref<T>) where T : Enum<T>, T : PrefValueHolder<S> {
    key(pref.key)
    defaultValue(pref.get().value)
}

fun <T : Any> AbstractPreferenceModel.Builder<*>.dependency(dependency: Pref<T>, value: T) {
    dependency(dependency.key, value, dependency.defaultValue)
}

fun <T, S : Any> AbstractPreferenceModel.Builder<*>.enumDependency(
    dependency: Pref<T>,
    value: T
) where T : Enum<T>, T : PrefValueHolder<S> {
    dependency(dependency.key, value.value, dependency.defaultValue.value)
}