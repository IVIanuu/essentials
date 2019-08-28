package com.ivianuu.essentials.ui.compose

import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.memo
import kotlin.reflect.KProperty

fun <T> ComponentComposition.holder(init: () -> T) = memo { Holder(init()) }

class Holder<T> internal constructor(var value: T) {

    operator fun component1(): T = value

    operator fun component2(): (T) -> Unit = { value = it }

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisObj: Any?, property: KProperty<*>, next: T) {
        value = next
    }

}
