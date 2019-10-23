package com.ivianuu.essentials.ui.compose.prefs

import androidx.compose.Ambient
import androidx.compose.Composable
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.kprefs.Pref

data class Dependency<T : Any>(
    val pref: Pref<T>,
    val value: T
) {

    fun isOk(): Boolean = pref.get() == value

}

data class Dependencies(val values: List<Dependency<*>>) {
    fun allOk(): Boolean = values.all { it.isOk() }
}

val DependenciesAmbient = Ambient.of { noDependencies }
private val noDependencies = Dependencies(emptyList())

@Composable
fun Dependencies(
    vararg dependencies: Pair<Pref<out Any>, Any>,
    children: @Composable() () -> Unit
) = composable("Dependencies") {
    DependenciesAmbient.Provider(
        value = Dependencies(
            dependencies
                .map { Dependency(it.first as Pref<Any>, it.second) }
                .toList()
        ),
        children = children
    )
}