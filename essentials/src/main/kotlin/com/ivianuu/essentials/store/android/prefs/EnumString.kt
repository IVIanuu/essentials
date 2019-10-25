/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.store.android.prefs

/*

inline fun <reified T> EnumStringPrefBox(
    key: String,
    defaultValue: T
): Pref<T> where T : Enum<T>, T : PrefValueHolder<String> =
    EnumStringPrefBox(key, defaultValue, T::class)

fun <T> EnumStringPrefBox(
    key: String,
    sharedPreferences: SharedPreferences,
    type: KClass<T>,
    defaultValue: T
): PrefBox<T> where T : Enum<T>, T : PrefValueHolder<String> =
    PrefBox(key, sharedPreferences, EnumStringPrefBoxAdapter(type, defaultValue), defaultValue)

private class EnumStringPrefBoxAdapter<T>(
    private val type: KClass<T>,
    private val defaultValue: T
) : PrefBox.Adapter<T> where T : Enum<T>, T : PrefValueHolder<String> {
    override fun get(key: String, preferences: SharedPreferences) =
        type.valueFor(preferences.getString(key, "")!!, defaultValue)

    override fun set(key: String, value: T, editor: SharedPreferences.Editor) {
        editor.putString(key, value.value)
    }
}*/