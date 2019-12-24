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

package com.ivianuu.essentials.ui.common

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ComposeAccessor
import androidx.compose.Composer
import androidx.compose.SlotEditor
import androidx.compose.SlotReader
import androidx.compose.composer
import com.ivianuu.essentials.util.cast

@Composable
fun pointInComposition(): Any {
    val composer = composer.composer

    val insertedProviders = Composer::class.java.getDeclaredField("insertedProviders")
        .also { it.isAccessible = true }
        .get(composer)
        .let { stack ->
            stack.javaClass.getDeclaredField("backing")
                .also { it.isAccessible = true }
                .get(stack)
                .cast<List<Any>>()
        }
        .map { holder -> ComposeAccessor.getAmbientFromHolder(holder) }

    val slots = Composer::class.java.getDeclaredField("slots")
        .also { it.isAccessible = true }
        .get(composer) as SlotReader

    val startStack = SlotEditor::class.java.getDeclaredField("startStack")
        .also { it.isAccessible = true }
        .get(slots)

    val existingProviders = mutableListOf<Ambient<*>>()

    var current = ComposeAccessor.intStackSize(startStack) - 1
    while (current > 0) {
        val index = ComposeAccessor.intStackPeek(startStack, current)
        val sentinel = slots.groupKey(index)
        if (sentinel === ComposeAccessor.getProviderKey()) {
            val element = slots.get(index + 1)
            if (ComposeAccessor.isAmbientHolder(element)) {
                existingProviders.add(0, ComposeAccessor.getAmbientFromHolder(element))
            }
        }
        current--
    }

    val providers = existingProviders + insertedProviders

    return providers.hashCode()
}
