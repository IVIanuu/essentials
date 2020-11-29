/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.ui.animatedstack

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.animatable.ProvideAnimatableRoot
import com.ivianuu.essentials.ui.animatable.animatable
import com.ivianuu.essentials.ui.animatable.animatableFor
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition

@Composable
fun <T> AnimatedBox(
    current: T,
    modifier: Modifier = Modifier,
    transition: StackTransition = FadeStackTransition(),
    item: @Composable (T) -> Unit
) {
    AnimatedStack(
        modifier = modifier,
        items = listOf(current),
        transition = transition,
        item = item
    )
}

@Composable
fun <T> AnimatedStack(
    items: List<T>,
    modifier: Modifier = Modifier,
    transition: StackTransition = FadeStackTransition(),
    item: @Composable (T) -> Unit
) {
    val children = remember(items, item) {
        items
            .map { item ->
                AnimatedStackChild(
                    key = item,
                    enterTransition = transition,
                    exitTransition = transition
                ) {
                    item(item)
                }
            }
    }
    AnimatedStack(modifier = modifier, children = children)
}

@Composable
fun <T> AnimatedStack(
    modifier: Modifier = Modifier,
    children: List<AnimatedStackChild<T>>,
) {
    ProvideAnimatableRoot {
        val state = remember { AnimatedStackState(children) }
        state.defaultTransition = DefaultStackTransitionAmbient.current
        state.setChildren(children)
        state.runningTransactions.values.toList().forEach {
            key(it) {
                it.run()
            }
        }
        Box(modifier = modifier) {
            state.visibleChildren.toList().forEach {
                key(it.key) {
                    it.content()
                }
            }
        }
    }
}

@Stable
internal class AnimatedStackState<T>(
    initialChildren: List<AnimatedStackChild<T>>
) {

    private var _children = initialChildren
    val visibleChildren = mutableStateListOf<AnimatedStackChild<T>>()

    var defaultTransition = NoOpStackTransition

    val runningTransactions = mutableStateMapOf<T, AnimatedStackTransaction<T>>()

    init {
        _children
            .filterVisible()
            .forEach {
                performChange(
                    from = null,
                    to = it,
                    isPush = true,
                    transition = NoOpStackTransition
                )
            }
    }

    fun setChildren(newChildren: List<AnimatedStackChild<T>>) {
        if (newChildren == _children) return

        // do not allow pushing the same child twice
        newChildren
            .groupBy { it }
            .forEach {
                check(it.value.size == 1) {
                    "Trying to push the same child multiple times $it"
                }
            }

        val oldChildren = _children.toList()
        val oldVisibleChildren = oldChildren.filterVisible()

        _children = newChildren

        val newVisibleChildren = newChildren.filterVisible()

        if (oldVisibleChildren != newVisibleChildren) {
            val oldTopChild = oldVisibleChildren.lastOrNull()
            val newTopChild = newVisibleChildren.lastOrNull()

            // check if we should animate the top children
            val replacingTopChildren = newTopChild != null && oldTopChild != newTopChild

            // Remove all visible children which shouldn't be visible anymore
            // from top to bottom
            oldVisibleChildren
                .dropLast(if (replacingTopChildren) 1 else 0)
                .reversed()
                .filterNot { it in newVisibleChildren }
                .forEach { child ->
                    val localTransition = child.exitTransition
                        ?: defaultTransition
                    performChange(
                        from = child,
                        to = null,
                        isPush = false,
                        transition = localTransition
                    )
                }

            // Add any new children to the stack from bottom to top
            newVisibleChildren
                .dropLast(if (replacingTopChildren) 1 else 0)
                .filterNot { it in oldVisibleChildren }
                .forEachIndexed { i, child ->
                    val localTransition =
                        child.enterTransition ?: defaultTransition
                    performChange(
                        from = newVisibleChildren.getOrNull(i - 1),
                        to = child,
                        isPush = true,
                        transition = localTransition
                    )
                }

            val isPush = newTopChild !in oldChildren

            // Replace the old visible top with the new one
            if (replacingTopChildren) {
                val localTransition =
                    if (isPush) newTopChild?.enterTransition ?: defaultTransition
                    else oldTopChild?.exitTransition ?: defaultTransition
                performChange(
                    from = oldTopChild,
                    to = newTopChild,
                    isPush = isPush,
                    transition = localTransition
                )
            }
        }
    }

    private fun List<AnimatedStackChild<T>>.filterVisible(): List<AnimatedStackChild<T>> {
        val visibleChildren = mutableListOf<AnimatedStackChild<T>>()

        for (child in reversed()) {
            visibleChildren += child
            if (!child.opaque) break
        }

        return visibleChildren
    }

    private fun performChange(
        from: AnimatedStackChild<T>?,
        to: AnimatedStackChild<T>?,
        isPush: Boolean,
        transition: StackTransition
    ) {
        from?.let { runningTransactions[it.key]?.cancel() }
        val transaction = AnimatedStackTransaction(from, to, isPush, transition, this)
        runningTransactions[transaction.transactionKey] = transaction
    }
}

@Stable
class AnimatedStackChild<T>(
    val key: T,
    val opaque: Boolean = false,
    val enterTransition: StackTransition? = null,
    val exitTransition: StackTransition? = null,
    val content: @Composable () -> Unit
) {

    var isAnimating by mutableStateOf(false)
        internal set

    @Composable
    internal fun content() {
        Box(modifier = Modifier.animatable(this)) {
            content.invoke()
        }
    }
}

@Stable
internal class AnimatedStackTransaction<T>(
    val from: AnimatedStackChild<T>?,
    val to: AnimatedStackChild<T>?,
    val isPush: Boolean,
    val transition: StackTransition,
    private val state: AnimatedStackState<T>
) {

    val transactionKey = when {
        to != null -> to.key
        from != null -> from.key
        else -> error("No transition needed")
    }

    private val removesFrom = from != null && (!isPush || !to!!.opaque)

    private var needsCompletion by mutableStateOf(false)
    private var completed by mutableStateOf(false)

    @Composable
    fun run() {
        remember { from?.isAnimating = true }

        val fromAnimatable = from?.let { animatableFor(it) }
        val toAnimatable = to?.let { animatableFor(it) }

        val context = remember {
            object : StackTransitionContext(
                fromAnimatable = fromAnimatable,
                toAnimatable = toAnimatable,
                isPush = isPush
            ) {
                override fun addTo() {
                    checkNotNull(to)
                    if (to !in state.visibleChildren)
                        this@AnimatedStackTransaction.addTo()
                }

                override fun removeFrom() {
                    checkNotNull(from)
                    if (from in state.visibleChildren)
                        this@AnimatedStackTransaction.removeFrom()
                }

                override fun onComplete() {
                    check(!needsCompletion) {
                        "onComplete() must be called only once"
                    }
                    if (!completed) needsCompletion = true
                }
            }
        }

        transition(context)

        if (needsCompletion && !completed) {
            complete()
        }
    }

    fun cancel() {
        complete()
    }

    fun complete() {
        if (to != null && to !in state.visibleChildren) addTo()
        if (removesFrom && from in state.visibleChildren) removeFrom()
        to?.isAnimating = false
        from?.isAnimating = false
        state.runningTransactions -= transactionKey
    }

    private fun addTo() {
        val to = to!!
        to.isAnimating = true

        val oldToIndex = state.visibleChildren.indexOf(to)
        val fromIndex = state.visibleChildren.indexOf(from)
        val toIndex = if (fromIndex != -1) {
            if (isPush) fromIndex + 1 else fromIndex
        } else if (oldToIndex != -1) oldToIndex else state.visibleChildren.size

        if (oldToIndex != toIndex) {
            if (oldToIndex != -1) state.visibleChildren.removeAt(oldToIndex)
            state.visibleChildren.add(toIndex, to)
        }
    }

    private fun removeFrom() {
        val from = from!!
        from.isAnimating = false
        state.visibleChildren -= from
    }
}
