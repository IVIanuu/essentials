/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.util

import android.content.SharedPreferences
import com.ivianuu.kprefs.KPrefs
import com.ivianuu.kprefs.Pref
import kotlin.reflect.KClass

interface PrefValueHolder<T> {
    val value: T
}

private fun <T, V> KClass<T>.findEnumFor(value: V): T where T : Enum<T>, T : PrefValueHolder<V> {
    val enums = java.enumConstants
    return enums.first { it.value == value }
}

private class EnumBooleanPrefAdapter<T>(
    private val clazz: KClass<T>
) : Pref.Adapter<T> where T : Enum<T>, T : PrefValueHolder<Boolean> {
    override fun get(key: String, preferences: SharedPreferences) =
        clazz.findEnumFor(preferences.getBoolean(key, false))

    override fun set(key: String, value: T, editor: SharedPreferences.Editor) {
        editor.putBoolean(key, value.value)
    }
}

private class EnumIntPrefAdapter<T>(
    private val clazz: KClass<T>
) : Pref.Adapter<T> where T : Enum<T>, T : PrefValueHolder<Int> {
    override fun get(key: String, preferences: SharedPreferences) =
        clazz.findEnumFor(preferences.getInt(key, 0))

    override fun set(key: String, value: T, editor: SharedPreferences.Editor) {
        editor.putInt(key, value.value)
    }
}

inline fun <reified T> KPrefs.enumInt(
    key: String,
    defaultValue: T
) where T : Enum<T>, T : PrefValueHolder<Int> =
    enumInt(key, defaultValue, T::class)

fun <T> KPrefs.enumInt(
    key: String,
    defaultValue: T,
    clazz: KClass<T>
) where T : Enum<T>, T : PrefValueHolder<Int> =
    custom(key, defaultValue, EnumIntPrefAdapter(clazz))

private class EnumFloatPrefAdapter<T>(
    private val clazz: KClass<T>
) : Pref.Adapter<T> where T : Enum<T>, T : PrefValueHolder<Float> {
    override fun get(key: String, preferences: SharedPreferences) =
        clazz.findEnumFor(preferences.getFloat(key, 0f))

    override fun set(key: String, value: T, editor: SharedPreferences.Editor) {
        editor.putFloat(key, value.value)
    }
}

inline fun <reified T> KPrefs.enumFloat(
    key: String,
    defaultValue: T
) where T : Enum<T>, T : PrefValueHolder<Float> =
    enumFloat(key, defaultValue, T::class)

fun <T> KPrefs.enumFloat(
    key: String,
    defaultValue: T,
    clazz: KClass<T>
) where T : Enum<T>, T : PrefValueHolder<Float> =
    custom(key, defaultValue, EnumFloatPrefAdapter(clazz))

private class EnumLongPrefAdapter<T>(
    private val clazz: KClass<T>
) : Pref.Adapter<T> where T : Enum<T>, T : PrefValueHolder<Long> {
    override fun get(key: String, preferences: SharedPreferences) =
        clazz.findEnumFor(preferences.getLong(key, 0L))

    override fun set(key: String, value: T, editor: SharedPreferences.Editor) {
        editor.putLong(key, value.value)
    }
}

inline fun <reified T> KPrefs.enumLong(
    key: String,
    defaultValue: T
) where T : Enum<T>, T : PrefValueHolder<Long> =
    enumLong(key, defaultValue, T::class)

fun <T> KPrefs.enumLong(
    key: String,
    defaultValue: T,
    clazz: KClass<T>
) where T : Enum<T>, T : PrefValueHolder<Long> =
    custom(key, defaultValue, EnumLongPrefAdapter(clazz))

private class EnumStringPrefAdapter<T>(
    private val clazz: KClass<T>
) : Pref.Adapter<T> where T : Enum<T>, T : PrefValueHolder<String> {
    override fun get(key: String, preferences: SharedPreferences) =
        clazz.findEnumFor(preferences.getString(key, "")!!)

    override fun set(key: String, value: T, editor: SharedPreferences.Editor) {
        editor.putString(key, value.value)
    }
}

inline fun <reified T> KPrefs.enumString(
    key: String,
    defaultValue: T
) where T : Enum<T>, T : PrefValueHolder<String> =
    enumString(key, defaultValue, T::class)

fun <T> KPrefs.enumString(
    key: String,
    defaultValue: T,
    clazz: KClass<T>
) where T : Enum<T>, T : PrefValueHolder<String> =
    custom(key, defaultValue, EnumStringPrefAdapter(clazz))

private class EnumStringSetPrefAdapter<T>(
    private val clazz: KClass<T>
) : Pref.Adapter<Set<T>> where T : Enum<T>, T : PrefValueHolder<String> {
    override fun get(key: String, preferences: SharedPreferences) =
        preferences.getStringSet(key, emptySet())!!
            .map { clazz.findEnumFor(it) }
            .toSet()

    override fun set(key: String, value: Set<T>, editor: SharedPreferences.Editor) {
        editor.putStringSet(key, value.map { it.value }.toSet())
    }
}

inline fun <reified T> KPrefs.enumStringSet(
    key: String,
    defaultValue: Set<T>
) where T : Enum<T>, T : PrefValueHolder<String> =
    enumStringSet(key, defaultValue, T::class)

fun <T> KPrefs.enumStringSet(
    key: String,
    defaultValue: Set<T>,
    clazz: KClass<T>
) where T : Enum<T>, T : PrefValueHolder<String> =
    custom(key, defaultValue, EnumStringSetPrefAdapter(clazz))