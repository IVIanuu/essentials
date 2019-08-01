package com.ivianuu.essentials.store

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

interface PrefBox<T> : Box<T> {

    val key: String

    val defaultValue: T

    interface Adapter<T> {
        fun read(sharedPreferences: SharedPreferences, key: String): T
        fun write(editor: SharedPreferences.Editor, key: String, value: T)
    }

}

internal object StringPrefBoxAdapter : PrefBox.Adapter<String> {
    override fun read(sharedPreferences: SharedPreferences, key: String): String =
        sharedPreferences.getString(key, null)!!

    override fun write(editor: SharedPreferences.Editor, key: String, value: String) {
        editor.putString(key, value)
    }
}

fun StringPrefBox(
    key: String,
    defaultValue: String = "",
    sharedPreferences: SharedPreferences
) = PrefBox(key, defaultValue, sharedPreferences, StringPrefBoxAdapter)

fun <T> PrefBox(
    key: String,
    defaultValue: T,
    sharedPreferences: SharedPreferences,
    adapter: PrefBox.Adapter<T>
): PrefBox<T> = PrefBoxImpl(key, defaultValue, sharedPreferences, adapter)

internal class PrefBoxImpl<T>(
    override val key: String,
    override val defaultValue: T,
    private val sharedPreferences: SharedPreferences,
    private val adapter: PrefBox.Adapter<T>
) : PrefBox<T> {

    override suspend fun get(): T {
        return if (exists()) {
            adapter.read(sharedPreferences, key)
        } else {
            defaultValue
        }
    }

    override suspend fun set(value: T) {
        sharedPreferences.edit()
            .apply { adapter.write(this, key, value) }
            .apply()
    }

    override suspend fun delete() {
        sharedPreferences.edit()
            .remove(key)
            .apply()
    }

    override suspend fun exists(): Boolean = sharedPreferences.contains(key)

    override fun asFlow(): Flow<T> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            launch { offer(get()) }
        }

        offer(get())

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        invokeOnClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }

    }

}