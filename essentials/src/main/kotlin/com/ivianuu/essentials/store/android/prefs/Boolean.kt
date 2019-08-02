package com.ivianuu.essentials.store.android.prefs

import android.content.SharedPreferences

fun BooleanPrefBox(
    key: String,
    sharedPreferences: SharedPreferences,
    defaultValue: Boolean = false
) = PrefBox(
    key,
    sharedPreferences,
    BooleanPrefBoxAdapter,
    defaultValue
)

private object BooleanPrefBoxAdapter : PrefBox.Adapter<Boolean> {
    override fun get(sharedPreferences: SharedPreferences, key: String): Boolean =
        sharedPreferences.getBoolean(key, false)

    override fun set(editor: SharedPreferences.Editor, key: String, value: Boolean) {
        editor.putBoolean(key, value)
    }
}