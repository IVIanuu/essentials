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

import android.content.SharedPreferences

fun IntPrefBox(
    key: String,
    sharedPreferences: SharedPreferences,
    defaultValue: Int = 0
) = PrefBox(
    key,
    sharedPreferences,
    IntPrefBoxAdapter,
    defaultValue
)

private object IntPrefBoxAdapter : PrefBox.Adapter<Int> {
    override fun get(sharedPreferences: SharedPreferences, key: String): Int =
        sharedPreferences.getInt(key, 0)

    override fun set(editor: SharedPreferences.Editor, key: String, value: Int) {
        editor.putInt(key, value)
    }
}