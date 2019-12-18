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

import androidx.compose.Composable
import androidx.compose.ComposeAccessor
import androidx.compose.State
import androidx.compose.composer
import androidx.compose.onActive
import androidx.compose.state

@Composable
fun pointInComposition(): Any? {
    val state: State<Any?> = state { null }
    val composer = composer.composer
    onActive {
        val slots = ComposeAccessor.getSlots(composer.slotTable).toList()
        val holder = slots
            .filterNotNull()
            .filter { ComposeAccessor.isCompositionLifecycleObserverHolder(it) }
            .single { holder -> ComposeAccessor.getInstance(holder) == this }

        val holderIndex = slots.indexOf(holder)
        val groupsWithIndex = slots.subList(0, holderIndex)
            .mapIndexedNotNull { index, value ->
                if (ComposeAccessor.isGroupStart(value)) value to index else null
            }

        val keys = mutableListOf<Any>()
        groupsWithIndex.forEach { (value, index) ->
            val groupSlots = ComposeAccessor.getSlots(value)
            if (holderIndex in index..index + groupSlots) {
                keys += ComposeAccessor.getKey(value)
            }
        }

        state.value = keys.hashCode()
    }

    return state.value
}
