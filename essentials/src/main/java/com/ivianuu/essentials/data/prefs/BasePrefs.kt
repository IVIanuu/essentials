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

package com.ivianuu.essentials.data.prefs

import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences

/**
 * Base preference class
 */
abstract class BasePrefs {
    abstract val rxPrefs: RxSharedPreferences
    
    protected fun booleanPref(key: String, defaultValue: Boolean = false): Preference<Boolean> =
        rxPrefs.getBoolean(key, defaultValue)

    protected fun <T : Enum<T>> enumPref(
        key: String, defaultValue: T,
        enumClass: Class<T>
    ): Preference<T> = rxPrefs.getEnum(key, defaultValue, enumClass)

    protected fun floatPref(key: String, defaultValue: Float = 0f): Preference<Float>
            = rxPrefs.getFloat(key, defaultValue)

    protected fun integerPref(key: String, defaultValue: Int = 0): Preference<Int>
            = rxPrefs.getInteger(key, defaultValue)
    
    protected fun longPref(key: String, defaultValue: Long = 0L): Preference<Long> 
            = rxPrefs.getLong(key, defaultValue)

    protected fun <T> objectPref(key: String, defaultValue: T, converter: Preference.Converter<T>): Preference<T>
            = rxPrefs.getObject(key, defaultValue, converter)

    protected fun stringPref(key: String, defaultValue: String = ""): Preference<String>
            = rxPrefs.getString(key, defaultValue)

    protected fun stringSetPref(key: String, defaultValue: Set<String> = emptySet()): Preference<Set<String>>
            = rxPrefs.getStringSet(key, defaultValue)
    
}