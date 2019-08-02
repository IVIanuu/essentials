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