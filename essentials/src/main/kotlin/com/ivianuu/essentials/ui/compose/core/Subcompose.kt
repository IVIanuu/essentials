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

import androidx.compose.Composable
import androidx.compose.Composer
import androidx.compose.ComposerAccessor
import androidx.compose.CompositionReference
import androidx.compose.Context
import androidx.compose.Emittable
import androidx.compose.Recomposer
import androidx.compose.ViewComposer
import androidx.compose.runWithCurrent
import com.github.ajalt.timberkt.d

fun getSubcompositions(container: Emittable): List<Subcomposition> =
    subcompositions
        .filter { it.key.emittable == container }
        .map { it.value }
        .toList()

fun recomposeAll(container: Emittable) {
    getSubcompositions(container).forEach {
        recomposeSubcomposition(container, it)
    }
}


fun recomposeSubcomposition(
    container: Emittable,
    tag: Any
): Boolean {
    val key = SubcompositionKey(container, tag)
    return subcompositions[key]!!.recompose()
}

fun setSubcompositionComposable(
    container: Emittable,
    tag: Any,
    composable: @Composable() () -> Unit
) {
    val key = SubcompositionKey(container, tag)
    val root = subcompositions.getValue(key)
    root.composable = composable
}

fun subcompose(
    context: Context,
    container: Emittable,
    parent: CompositionReference,
    tag: Any,
    composable: @Composable() () -> Unit
) {
    d { "subcompose start $tag" }
    val key = SubcompositionKey(container, tag)
    var root = subcompositions[key]

    if (root == null) {
        root = Subcomposition(tag)
        root.composer = ViewComposer(container, context, Recomposer.current()).apply {
            ComposerAccessor.setParentReference(this, parent)
            parent.registerComposer(this)
        }
        subcompositions[key] = root
    }

    root.composable = composable
    root.update()

    d { "subcompose end $tag" }
}

fun disposeSubcomposition(
    context: Context,
    container: Emittable,
    parent: CompositionReference,
    tag: Any
) {
    d { "dispose composition start $tag" }
    val key = SubcompositionKey(container, tag)
    check(key in subcompositions.keys)
    subcompose(context, container, parent, tag) {}
    subcompositions.remove(key)
    d { "dispose composition end $tag" }
}

class Subcomposition(private val tag: Any) {

    lateinit var composable: @Composable() () -> Unit
    lateinit var composer: Composer<*>

    fun recompose(): Boolean {
        if (ComposerAccessor.isComposing(composer)) return false
        return composer.runWithCurrent {
            val hadChanges: Boolean
            try {
                ComposerAccessor.setComposing(composer, true)
                hadChanges = composer.recompose()
                composer.applyChanges()
            } finally {
                ComposerAccessor.setComposing(composer, false)
            }
            hadChanges
        }
    }

    fun update() {
        d { "update start $tag" }
        composer.runWithCurrent {
            val composerWasComposing = ComposerAccessor.isComposing(composer)
            try {
                ComposerAccessor.setComposing(composer, true)
                composer.startRoot()
                composer.startGroup("invocation")
                composable()
                composer.endGroup()
                composer.endRoot()
            } finally {
                ComposerAccessor.setComposing(composer, composerWasComposing)
            }

            composer.applyChanges()
        }
        d { "update end $tag" }
    }

}

private data class SubcompositionKey(
    val emittable: Emittable,
    val tag: Any
)

private val subcompositions = mutableMapOf<SubcompositionKey, Subcomposition>()