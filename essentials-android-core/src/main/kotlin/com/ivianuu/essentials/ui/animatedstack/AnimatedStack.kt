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
        val state = remember { AnimatedStackState(children) }
        state.defaultTransition = DefaultStackTransitionAmbient.current
        state.setChildren(children)
        state.runningTransactions.values.toList().forEach { it.run() }
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
internal class AnimatedStackState<T>(
    initialChildren: List<AnimatedStackChild<T>>
) {

    private var _children = initialChildren
    val visibleChildren = modelListOf<AnimatedStackChild<T>>()

    var defaultTransition = NoOpStackTransition

    val runningTransactions = modelMapOf<T, AnimatedStackTransaction<T>>()

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
        Box(
            modifier = Modifier.animatable(this),
            children = content
        )
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

    private val transactionId = UUID.randomUUID()
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
        key(transactionId) {
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