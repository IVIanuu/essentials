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

package com.ivianuu.essentials.util.ext

import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

@Suppress("UNCHECKED_CAST")
fun <T : Preference> PreferenceFragmentCompat.findPref(key: CharSequence): T? =
        findPreference(key) as T?

fun <T : Preference> PreferenceFragmentCompat.requirePref(key: CharSequence): T =
        findPref(key) ?: throw IllegalStateException("pref not found $key")