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