/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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