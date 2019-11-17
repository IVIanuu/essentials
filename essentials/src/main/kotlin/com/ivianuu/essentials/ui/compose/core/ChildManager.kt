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

import android.content.Context
import androidx.compose.Composable
import androidx.compose.CompositionReference
import androidx.compose.Recomposer
import androidx.compose.RecomposerAccessor
import androidx.compose.ambient
import androidx.compose.composer
import androidx.compose.compositionReference
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.onDispose
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.core.LayoutNode
import androidx.ui.core.ParentData
import com.github.ajalt.timberkt.d
import kotlin.math.max

fun childManager() = effectOf<ChildManager> {
    val context = +ambient(ContextAmbient)
    val compositionReference = +compositionReference()

    lateinit var childManager: ChildManager
    val reference = object : CompositionReference by compositionReference {
        override fun invalidate(sync: Boolean) {
            val composer = getSubcompositions(childManager.layoutNode!!).first().composer
            if (sync) {
                RecomposerAccessor.recomposeSync(Recomposer.current(), composer)
            } else {
                RecomposerAccessor.scheduleRecompose(Recomposer.current(), composer)
            }
        }
    }

    childManager = +memo {
        ChildManager(
            context = context,
            compositionReference = reference
        )
    }

    // dispose
    +onDispose { childManager.dispose() }

    /*// compose
    if (childManager.layoutNode != null) {
        childManager.recompose()
    }*/

    return@effectOf childManager
}

class ChildManager(
    private val context: Context,
    private val compositionReference: CompositionReference
) {

    var layoutNode: LayoutNode? = null
        private set
    private val children = mutableMapOf<Any, @Composable() () -> Unit>()

    fun setLayoutNode(layoutNode: LayoutNode) {
        this.layoutNode = layoutNode
    }

    fun add(key: Any, composable: @Composable() () -> Unit) {
        add(max(children.size, 0), key, composable)
    }

    fun add(index: Int, key: Any, composable: @Composable() () -> Unit) {
        val finalComposable: @Composable() () -> Unit = {
            ParentData(
                data = +memo { ChildManagerParentData(key) },
                children = composable
            )
        }
        val childrenToIterate = children.toList().toMutableList()
        children[key] = finalComposable
        childrenToIterate.add(index, key to finalComposable)
        d { "add at $index children to iterate $childrenToIterate" }
        subcompose {
            childrenToIterate.forEachIndexed { currentIndex, (key, composable) ->
                childComposable(
                    key = key,
                    skip = currentIndex != index,
                    composable = composable
                )
            }
        }

        setDefaultComposable()
        sortChildren()
    }

    fun get(key: Any): LayoutNode? = layoutNode!!.layoutChildren.firstOrNull {
        (it.parentData as ChildManagerParentData).key == key
    }

    fun getAll(): Map<Any, LayoutNode> = layoutNode!!.layoutChildren.associateBy {
        (it.parentData as ChildManagerParentData).key
    }

    fun move(from: Any, to: Any) {
        TODO()
    }

    fun remove(key: Any) {
        d { "remove key $key" }
        children.remove(key)
        subcompose {
            children.forEach { (key, composable) ->
                childComposable(
                    key = key,
                    skip = true,
                    composable = composable
                )
            }
        }
        setDefaultComposable()
        sortChildren()
    }

    fun removeAll(keys: List<Any>) {
        children.remove(keys)
        subcompose {
            children.forEach { (key, composable) ->
                childComposable(
                    key = key,
                    skip = true,
                    composable = composable
                )
            }
        }
        setDefaultComposable()
        sortChildren()
    }

    fun clear() {
        children.clear()
        subcompose {}
    }

    fun recompose() {
        setDefaultComposable()
        recomposeSubcomposition(layoutNode!!, "tag")
    }

    fun dispose() {
        disposeSubcomposition(context, layoutNode!!, compositionReference, "tag")
    }

    private fun subcompose(
        block: @Composable() () -> Unit
    ) {
        subcompose(context, layoutNode!!, compositionReference, "tag", block)
    }

    private fun sortChildren() {
        d { "children pre sort $children layout ${layoutNode!!.layoutChildren.map { it.parentData }}" }
        val unsortedChildren = children.toMap()
        children.clear()
        layoutNode!!.layoutChildren.forEach {
            val key = (it.parentData as ChildManagerParentData).key
            d { "process node $key" }
            val composable = unsortedChildren[key] ?: return@forEach
            children[key] = composable
        }
        d { "children post sort $children" }
    }

    private fun setDefaultComposable() {
        setSubcompositionComposable(layoutNode!!, "tag") {
            children.forEach { (key, composable) ->
                childComposable(
                    key = key,
                    skip = false,
                    composable = composable
                )
            }
        }
    }

    private fun childComposable(
        key: Any,
        skip: Boolean,
        composable: @Composable() () -> Unit
    ) {
        with(composer.composer) {
            d { "child composable $key skip $skip" }
            startGroup(key)
            if (inserting || !skip) {
                startGroup(invocation)
                composable()
                endGroup()
            } else {
                skipCurrentGroup()
            }
            endGroup()
        }
    }
}

data class ChildManagerParentData(val key: Any, val data: Any? = null)

private val invocation = Any()