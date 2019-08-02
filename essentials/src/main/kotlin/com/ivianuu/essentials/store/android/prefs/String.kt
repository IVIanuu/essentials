package com.ivianuu.essentials.store.android.prefs

import android.content.SharedPreferences

fun StringPrefBox(
    key: String,
    sharedPreferences: SharedPreferences,
    defaultValue: String = ""
) = PrefBox(
    key,
    sharedPreferences,
    StringPrefBoxAdapter,
    defaultValue
)

private object StringPrefBoxAdapter : PrefBox.Adapter<String> {
    override fun get(sharedPreferences: SharedPreferences, key: String): String =
        sharedPreferences.getString(key, null)!!

    override fun set(editor: SharedPreferences.Editor, key: String, value: String) {
        editor.putString(key, value)
    }
}