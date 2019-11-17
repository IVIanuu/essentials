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
import androidx.compose.Observe
import androidx.compose.ambient
import androidx.compose.compositionReference
import androidx.compose.memo
import androidx.compose.onPreCommit
import androidx.compose.unaryPlus
import androidx.ui.core.ContextAmbient
import androidx.ui.core.LayoutNode
import androidx.ui.core.ParentData
import com.github.ajalt.timberkt.d
import kotlin.math.max

@Composable
fun ChildManager(
    body: @Composable() (ChildManager) -> Unit
) = composable("ChildManager") {
    val context = +ambient(ContextAmbient)
    var reference: CompositionReference? = null
    val childManagerRef = +ref<ChildManager?> { null }

    Observe {
        d { "invoke observe" }
        reference = +compositionReference()
        childManagerRef.value?.recompose()
        +onPreCommit(true) {
            onDispose {
                childManagerRef.value!!.dispose()
            }
        }
    }

    childManagerRef.value = +memo {
        ChildManager(
            context = context,
            compositionReference = reference!!
        )
    }

    body(childManagerRef.value!!)
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
            ParentData(data = +memo { ChildManagerParentData(key) }) {
                composable()
            }
        }
        val childrenToIterate = children.toList().toMutableList()
        children[key] = finalComposable
        childrenToIterate.add(index, key to finalComposable)
        d { "add at $index children to iterate $childrenToIterate" }
        compose()
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
        compose()
        sortChildren()
    }

    fun removeAll(keys: List<Any>) {
        keys.forEach { children.remove(it) }
        compose()
        sortChildren()
    }

    fun clear() {
        children.clear()
        compose()
    }

    fun recompose() {
        recomposeSubcomposition(layoutNode!!, "tag")
    }

    fun dispose() {
        disposeSubcomposition(context, layoutNode!!, compositionReference, "tag")
    }

    private fun compose() {
        subcompose(context, layoutNode!!, compositionReference, "tag") {
            children.forEach { (key, composable) ->
                childComposable(key, composable)
            }
        }
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

    @Composable
    private fun childComposable(
        key: Any,
        composable: @Composable() () -> Unit
    ) {
        composable(key) {
            composable()
        }
    }
}

data class ChildManagerParentData(val key: Any, var data: Any? = null)