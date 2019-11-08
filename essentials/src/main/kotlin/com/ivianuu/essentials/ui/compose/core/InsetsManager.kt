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

package com.ivianuu.essentials.ui.compose.core

import android.view.View
import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.onActive
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.core.view.WindowInsetsCompat
import com.github.ajalt.timberkt.d
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import dev.chrisbanes.insetter.requestApplyInsetsWhenAttached

class InsetsManager internal constructor(private val view: View) {

    private val listeners = mutableListOf<(WindowInsetsCompat) -> Unit>()

    init {
        view.doOnApplyWindowInsets { _, insets, _ ->
            d { "on apply window insets $insets" }
            listeners.toList().forEach { it(insets) }
        }
    }

    fun registerInsetsListener(
        listener: (WindowInsetsCompat) -> Unit
    ) {
        listeners += listener
        view.requestApplyInsetsWhenAttached()
    }

    fun unregisterInsetsListener(
        listener: (WindowInsetsCompat) -> Unit
    ) {
        listeners -= listener
        view.requestApplyInsetsWhenAttached()
    }

}

val InsetsManagerAmbient = Ambient.of<InsetsManager>()

fun handleInsets(
    listener: (WindowInsetsCompat) -> Unit
) = effectOf<Unit> {
    val insetsManager = +ambient(InsetsManagerAmbient)

    val listenerHolder = +ref { listener }
    listenerHolder.value = listener

    val internalListener: (WindowInsetsCompat) -> Unit = +memo {
        { insets: WindowInsetsCompat -> listenerHolder.value(insets) }
    }

    +onActive {
        insetsManager.registerInsetsListener(internalListener)
        onDispose { insetsManager.unregisterInsetsListener(internalListener) }
    }
}

@Composable
fun WithInsets(
    children: @Composable() (WindowInsetsCompat) -> Unit
) = composable("WithInsets") {
    val latestInsets = +state<WindowInsetsCompat?> { null }
    +handleInsets { latestInsets.value = it }
    if (latestInsets.value != null) {
        children(latestInsets.value!!)
    }
}