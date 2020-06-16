package com.ivianuu.essentials.ui.animatedstack

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
import androidx.compose.frames.modelMapOf
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.layout.Stack
import com.ivianuu.essentials.ui.animatable.AnimatableRoot
import com.ivianuu.essentials.ui.animatable.animatable
import com.ivianuu.essentials.ui.animatable.animatableFor
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.common.untrackedState
import java.util.UUID

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
    val lastChildren = untrackedState {
        emptyList<AnimatedStackChild<T>>()
    }
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
    lastChildren.value = children
    AnimatedStack(modifier = modifier, children = children)
}

@Composable
fun <T> AnimatedStack(
    modifier: Modifier = Modifier,
    children: List<AnimatedStackChild<T>>
) {
    AnimatableRoot {
        val state = remember { AnimatedStackState<T>() }
        state.defaultTransition = DefaultStackTransitionAmbient.current
        state.setChildren(children)
        state.activeTransitions.values.toList().forEach { it() }
        Stack(modifier = modifier) {
            state.visibleChildren.toList().forEach {
                key(it.key) {
                    it.content()
                }
            }
        }
    }
}

@Stable
internal class AnimatedStackState<T> {

    private var _children = emptyList<AnimatedStackChild<T>>()
    val visibleChildren = modelListOf<AnimatedStackChild<T>>()

    var defaultTransition = NoOpStackTransition

    val activeTransitions = modelMapOf<T, @Composable () -> Unit>()

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
        val exitFrom = from != null && (!isPush || !to!!.opaque)

        from?.key?.let { activeTransitions -= it }

        val transactionId = UUID.randomUUID()
        val transactionKey = to?.key ?: from?.key ?: error("No transition needed")

        activeTransitions[transactionKey] = {
            key(transactionId) {
                val fromAnimatable = from?.let { animatableFor(it) }
                val toAnimatable = to?.let { animatableFor(it) }

                var completed by state { false }
                if (completed) {
                    remember {
                        activeTransitions -= transactionKey
                        to?.onTransitionComplete(this)
                        from?.onTransitionComplete(this)
                    }
                }

                val context = remember {
                    object : StackTransitionContext(
                        fromAnimatable = fromAnimatable,
                        toAnimatable = toAnimatable,
                        isPush = isPush
                    ) {
                        override fun addTo() {
                            checkNotNull(to)
                            to.enter(state = this@AnimatedStackState, from = from, isPush = isPush)
                        }

                        override fun removeFrom() {
                            checkNotNull(from)
                            if (exitFrom) from.exit(
                                state = this@AnimatedStackState,
                                to = to,
                                isPush = isPush
                            )
                        }

                        override fun onComplete() {
                            check(!completed) {
                                "onComplete() must be called only once"
                            }
                            completed = true
                        }
                    }
                }

                transition(context)
            }
        }
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
        private set

    private var removeOnComplete = false

    @Composable
    internal fun content() {
        Box(
            modifier = Modifier.animatable(this),
            children = content
        )
    }

    internal fun enter(
        state: AnimatedStackState<T>,
        from: AnimatedStackChild<T>?,
        isPush: Boolean
    ) {
        isAnimating = true

        val oldToIndex = state.visibleChildren.indexOf(this)

        val fromIndex = state.visibleChildren.indexOf(from)
        val toIndex = if (fromIndex != -1) {
            if (isPush) fromIndex + 1 else fromIndex
        } else if (oldToIndex != -1) oldToIndex else state.visibleChildren.size

        if (oldToIndex != toIndex) {
            if (oldToIndex != -1) state.visibleChildren.removeAt(oldToIndex)
            state.visibleChildren.add(toIndex, this)
        }

        removeOnComplete = false
    }

    internal fun exit(
        state: AnimatedStackState<T>,
        to: AnimatedStackChild<T>?,
        isPush: Boolean
    ) {
        isAnimating = true
        removeOnComplete = true
    }

    internal fun onTransitionComplete(
        state: AnimatedStackState<T>
    ) {
        isAnimating = false
        if (removeOnComplete) {
            removeOnComplete = false
            state.visibleChildren.remove(this)
        }
    }

}
