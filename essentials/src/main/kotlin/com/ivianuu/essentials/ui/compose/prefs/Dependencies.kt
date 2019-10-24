package com.ivianuu.essentials.ui.compose.prefs

import com.ivianuu.kprefs.Pref

data class Dependency<T : Any>(
    val pref: Pref<T>,
    val value: T
) {
    fun check(): Boolean = pref.get() == value
}