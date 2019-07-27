package com.ivianuu.essentials.ui.compose.core

import androidx.compose.ComposerUpdater

inline class Updater<T : Any>(val backing: ComposerUpdater<Any, T>) {

    inline fun <reified V> set(
        value: V,
        crossinline block: T.(value: V) -> Unit
    ) = backing.set(value, block)

    // todo remove?
    inline fun init(crossinline block: T.() -> Unit) = backing.set(Unit) {
        block()
    }

    inline fun <reified V> update(
        value: V,
        crossinline block: T.(value: V) -> Unit
    ) = backing.update(value, block)

}