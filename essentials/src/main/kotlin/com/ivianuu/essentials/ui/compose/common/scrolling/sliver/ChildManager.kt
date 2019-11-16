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

package com.ivianuu.essentials.ui.compose.common.scrolling.sliver

import android.content.Context
import androidx.compose.Composable
import androidx.compose.CompositionReference
import androidx.ui.core.LayoutNode
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.compose.core.Ref
import com.ivianuu.essentials.ui.compose.core.disposeSubcomposition
import com.ivianuu.essentials.ui.compose.core.subcompose

interface IndexedParentData {
    val index: Int
}

val LayoutNode.indexedParentData: IndexedParentData get() = parentData as IndexedParentData

class ChildManager(
    val tag: String,
    val context: Context,
    val compositionReference: CompositionReference,
    val layoutNodeRef: Ref<LayoutNode?>
) {

    private val children = mutableMapOf<Int, @Composable() () -> Unit>()

    init {
        d { "$tag init" }
    }

    @Composable
    fun compose() {
        d { "$tag compose ${children.keys}" }
        children.forEach { (index, composable) ->
            subcompose(context, layoutNodeRef.value!!, compositionReference, index) {
                composable()
            }
        }

        check(
            children.keys.all { index ->
                layoutNodeRef.value!!.layoutChildren.firstOrNull { it.indexedParentData.index == index } != null
            }
        ) {
            "inconsistency has ${children.keys} but layout children are ${layoutNodeRef.value!!.layoutChildren.map { it.indexedParentData.index }}"
        }

        d { "after compose children ${layoutNodeRef.value!!.layoutChildren}" }
    }

    fun addChild(index: Int, composable: @Composable() () -> Unit): LayoutNode {
        d { "$tag add child $index all children $children" }
        check(index !in children.keys)
        children[index] = composable
        subcompose(context, layoutNodeRef.value!!, compositionReference, index, composable)

        return getChild(index)!!
    }

    fun removeChild(index: Int) {
        d { "$tag remove child $index" }
        children.remove(index)
        disposeSubcomposition(context, layoutNodeRef.value!!, compositionReference, index)
    }

    fun getChild(index: Int): LayoutNode? {
        d { "$tag get child $index all children $children" }
        val result =
            layoutNodeRef.value!!.layoutChildren.firstOrNull { it.indexedParentData.index == index }
        if (result == null) {
            check(index !in children.keys) {
                "child not in layout children but in children -> ${children.keys} layout children -> ${layoutNodeRef.value!!.layoutChildren.map { it.indexedParentData.index }}"
            }
        }
        return result
    }
}

