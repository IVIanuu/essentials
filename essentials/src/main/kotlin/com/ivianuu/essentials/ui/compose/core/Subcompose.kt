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
import androidx.compose.Composer
import androidx.compose.ComposerAccessor
import androidx.compose.CompositionReference
import androidx.compose.Context
import androidx.compose.Emittable
import androidx.compose.FrameManager
import androidx.compose.Recomposer
import androidx.compose.ViewComposer
import androidx.compose.runWithCurrent
import androidx.compose.trace
import com.github.ajalt.timberkt.d

fun getSubcompositions(container: Emittable): List<Composer<*>> =
    subcompositions
        .filterKeys { it.emittable == container }
        .map { it.value.composer }
        .toList()

fun subcompose(
    context: Context,
    container: Emittable,
    parent: CompositionReference,
    tag: Any? = null,
    composable: @Composable() () -> Unit
): Composer<*> {
    val key = SubcompositionKey(container, tag)
    var root = subcompositions[key]
    val wasInit = root == null

    if (root == null) {
        root = SubcompositionRoot()
        root.composer = ViewComposer(container, context, Recomposer.current()).apply {
            ComposerAccessor.setParentReference(this, parent)
            parent.registerComposer(this)
        }
        subcompositions[key] = root
    }

    root.composable = composable
    if (wasInit) root.init() else root.update()

    return root.composer
}

fun disposeSubcomposition(
    context: Context,
    container: Emittable,
    parent: CompositionReference,
    tag: Any?
) {
    d { "dispose composition start" }
    val key = SubcompositionKey(container, tag)
    subcompose(context, container, parent, tag) {}
    subcompositions.remove(key)
    d { "dispose composition end" }
}

private class SubcompositionRoot : Component() {
    fun update() {
        d { "update start" }
        composer.runWithCurrent {
            composer.recompose()
            composer.applyChanges()
        }
        d { "update end" }
    }

    fun init() {
        d { "init start" }
        composer.runWithCurrent {
            val composerWasComposing = ComposerAccessor.isComposing(composer)
            try {
                ComposerAccessor.setComposing(composer, true)
                trace("Compose:recompose") {
                    composer.startRoot()
                    composer.startGroup("invocation")
                    this()
                    composer.endGroup()
                    composer.endRoot()
                }
            } finally {
                ComposerAccessor.setComposing(composer, composerWasComposing)
            }
            // TODO(b/143755743)
            composer.applyChanges()
            if (!composerWasComposing) {
                FrameManager.nextFrame()
            }
        }
        d { "init end" }
    }

    lateinit var composable: @Composable() () -> Unit
    lateinit var composer: Composer<*>
    @Suppress("PLUGIN_ERROR")
    override fun compose() {
        d { "root composing start" }
        with(ComposerAccessor.currentComposerNonNull()) {
            startGroup(0)
            composable()
            endGroup()
        }
        d { "root composing end" }
    }
}

private data class SubcompositionKey(
    val emittable: Emittable,
    val tag: Any?
)

private val subcompositions = mutableMapOf<SubcompositionKey, SubcompositionRoot>()