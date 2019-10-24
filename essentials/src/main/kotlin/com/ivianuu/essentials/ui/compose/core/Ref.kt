package com.ivianuu.essentials.ui.compose.core

import androidx.compose.memo
import kotlin.reflect.KProperty

fun <T> ref(init: () -> T) = memo { Ref(init()) }

fun <T> refFor(vararg inputs: Any?, init: () -> T) =
    memo(*inputs) { Ref(init()) }

class Ref<T>(var value: T) {

    operator fun component1(): T = value

    operator fun component2(): (T) -> Unit = { value = it }
    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }
}