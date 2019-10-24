package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Composable
import androidx.ui.core.Opacity
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

data class Dependency<T : Any>(
    val pref: Pref<T>,
    val value: T
) {
    fun check(): Boolean = pref.get() == value
}

fun List<Dependency<*>>?.checkAll(): Boolean = this?.all { it.check() } ?: true

@Composable
fun Dependencies(
    dependencies: List<Dependency<*>>? = null,
    child: @Composable() (dependenciesOk: Boolean) -> Unit
) = composable("Dependencies") {
    val dependenciesOk = dependencies.checkAll()
    Opacity(if (dependenciesOk) 1f else 0.5f) {
        child(dependenciesOk)
    }
}