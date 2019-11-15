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

import androidx.compose.Component
import androidx.compose.Composable
import androidx.compose.ComposerAccessor
import androidx.compose.CompositionContext
import androidx.compose.CompositionReference
import androidx.compose.Context
import androidx.compose.Emittable
import com.github.ajalt.timberkt.d

fun getSubcompositions(container: Emittable): List<CompositionContext> =
    subcompositions
        .filterKeys { it.emittable == container }
        .map { it.value.context }
        .toList()

fun subcompose(
    context: Context,
    container: Emittable,
    parent: CompositionReference,
    tag: Any? = null,
    composable: @Composable() () -> Unit
): CompositionContext {
    val key = SubcompositionKey(container, tag)
    var root = subcompositions[key]

    if (root == null) {
        root = SubcompositionRoot()
        root.context = CompositionContext.prepare(context, container, root, parent)
        subcompositions[key] = root
        d { "sub compositions $subcompositions" }
    }

    root.composable = composable
    root.update()

    return root.context
}

fun disposeSubcomposition(
    container: Emittable,
    tag: Any?
) {
    subcompositions.remove(SubcompositionKey(container, tag))?.let {
        it.composable = {}
        it.compose()
    }
}

private class SubcompositionRoot : Component() {
    fun update() {
        val wasComposing = ComposerAccessor.isComposing(context.composer)
        ComposerAccessor.setComposing(context.composer, true)
        context.compose()
        ComposerAccessor.setComposing(context.composer, wasComposing)
    }

    lateinit var composable: @Composable() () -> Unit
    lateinit var context: CompositionContext
    @Suppress("PLUGIN_ERROR")
    override fun compose() {
        with(ComposerAccessor.currentComposerNonNull()) {
            startGroup(0)
            composable()
            endGroup()
        }
    }
}

private data class SubcompositionKey(
    val emittable: Emittable,
    val tag: Any?
)

private val subcompositions = mutableMapOf<SubcompositionKey, SubcompositionRoot>()