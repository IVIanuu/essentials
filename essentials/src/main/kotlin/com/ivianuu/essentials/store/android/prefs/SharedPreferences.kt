package com.ivianuu.essentials.store.android.prefs

import android.content.SharedPreferences
import com.ivianuu.essentials.store.Box
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

interface PrefBox<T> : Box<T> {

    val key: String

    val defaultValue: T

    interface Adapter<T> {
        fun get(sharedPreferences: SharedPreferences, key: String): T
        fun set(editor: SharedPreferences.Editor, key: String, value: T)
    }

}

fun <T> PrefBox(
    key: String,
    sharedPreferences: SharedPreferences,
    adapter: PrefBox.Adapter<T>,
    defaultValue: T
): PrefBox<T> =
    PrefBoxImpl(
        key,
        defaultValue,
        sharedPreferences,
        adapter
    )

internal class PrefBoxImpl<T>(
    override val key: String,
    override val defaultValue: T,
    private val sharedPreferences: SharedPreferences,
    private val adapter: PrefBox.Adapter<T>
) : PrefBox<T> {

    override suspend fun get(): T {
        return if (exists()) {
            adapter.get(sharedPreferences, key)
        } else {
            defaultValue
        }
    }

    override suspend fun set(value: T) {
        sharedPreferences.edit()
            .apply { adapter.set(this, key, value) }
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