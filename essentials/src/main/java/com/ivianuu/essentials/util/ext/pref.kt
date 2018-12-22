package com.ivianuu.essentials.util.ext

import com.ivianuu.epoxyprefs.PreferenceModel
import com.ivianuu.epoxyprefs.dependency
import com.ivianuu.essentials.util.PrefValueHolder
import com.ivianuu.kprefs.Pref

fun <T : Any> PreferenceModel.Builder.fromPref(pref: Pref<T>) {
    key(pref.key)
    defaultValue(pref.defaultValue)
}

fun <T, S> PreferenceModel.Builder.fromEnumPref(pref: Pref<T>) where T : Enum<T>, T : PrefValueHolder<S> {
    key(pref.key)
    defaultValue(pref.get().value)
}

fun <T : Any> PreferenceModel.Builder.dependency(dependency: Pref<T>, value: T) {
    dependency(dependency.key, value, dependency.defaultValue)
}

fun <T, S> PreferenceModel.Builder.enumDependency(
    dependency: Pref<T>,
    value: T
) where T : Enum<T>, T : PrefValueHolder<S> {
    dependency(dependency.key, value, dependency.defaultValue.value)
}