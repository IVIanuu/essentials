package com.ivianuu.essentials.ui.animatedstack

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
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
    modifier: Modifier = Modifier,
    item: T,
    transition: StackTransition = FadeStackTransition(),
    itemCallback: @Composable (T) -> Unit
) {
    AnimatedStack(
        modifier = modifier,
        items = listOf(item),
        transition = transition,
        itemCallback = itemCallback
    )
}

@JvmName("AnimatedStackT")
@Composable
fun <T> AnimatedStack(
    modifier: Modifier = Modifier,
    items: List<T>,
    transition: StackTransition = FadeStackTransition(),
    itemCallback: @Composable (T) -> Unit
) {
    var lastEntries by untrackedState { listOf<AnimatedStackEntry>() }

    val entries = items.map { item ->
        lastEntries.firstOrNull { entry -> entry.key == item } ?: AnimatedStackEntry(
            key = item,
            opaque = false,
            enterTransition = transition,
            exitTransition = transition,
            content = { itemCallback(item) }
        )
    }
    lastEntries = entries

    AnimatedStack(modifier = modifier, entries = entries)
}

@Composable
fun AnimatedStack(
    modifier: Modifier = Modifier,
    entries: List<AnimatedStackEntry>
) {
    AnimatableRoot {
        val state = remember { AnimatedStackState() }
        state.defaultTransition = DefaultStackTransitionAmbient.current
        state.setEntries(entries)
        state.activeTransitions.forEach { it() }
        Stack(modifier = modifier) {
            state.visibleEntries.forEach {
                key(it.entry.key) {
                    it.content()
                }
            }
        }
    }
}

private class AnimatedStackState {

    private var _entries = emptyList<AnimatedStackEntryState>()
    val visibleEntries = modelListOf<AnimatedStackEntryState>()

    var defaultTransition = NoOpStackTransition

    val activeTransitions = modelListOf<@Composable () -> Unit>()

    fun setEntries(newEntries: List<AnimatedStackEntry>) {
        setEntriesInternal(
            newEntries.map { newEntry ->
                _entries.firstOrNull { it.entry == newEntry } ?: AnimatedStackEntryState(newEntry)
            }
        )
    }

    private fun setEntriesInternal(newEntries: List<AnimatedStackEntryState>) {
        if (newEntries == _entries) return

        // do not allow pushing the same entry twice
        newEntries
            .groupBy { it }
            .forEach {
                check(it.value.size == 1) {
                    "Trying to push the same entry multiple times $it"
                }
            }

        val oldEntries = _entries.toList()
        val oldVisibleEntries = oldEntries.filterVisible()

        _entries = newEntries

        val newVisibleEntries = newEntries.filterVisible()

        if (oldVisibleEntries != newVisibleEntries) {
            val oldTopEntry = oldVisibleEntries.lastOrNull()
            val newTopEntry = newVisibleEntries.lastOrNull()

            // check if we should animate the top entries
            val replacingTopEntries = newTopEntry != null && oldTopEntry != newTopEntry

            // Remove all visible entries which shouldn't be visible anymore
            // from top to bottom
            oldVisibleEntries
                .dropLast(if (replacingTopEntries) 1 else 0)
                .reversed()
                .filterNot { it in newVisibleEntries }
                .forEach { entry ->
                    val localTransition = entry.entry.exitTransition
                        ?: defaultTransition
                    performChange(
                        from = entry,
                        to = null,
                        isPush = false,
                        transition = localTransition
                    )
                }

            // Add any new entries to the stack from bottom to top
            newVisibleEntries
                .dropLast(if (replacingTopEntries) 1 else 0)
                .filterNot { it in oldVisibleEntries }
                .forEachIndexed { i, entry ->
                    val localTransition =
                        entry.entry.enterTransition ?: defaultTransition
                    performChange(
                        from = newVisibleEntries.getOrNull(i - 1),
                        to = entry,
                        isPush = true,
                        transition = localTransition
                    )
                }

            val isPush = newTopEntry !in oldEntries

            // Replace the old visible top with the new one
            if (replacingTopEntries) {
                val localTransition =
                    if (isPush) newTopEntry?.entry?.enterTransition ?: defaultTransition
                    else oldTopEntry?.entry?.exitTransition ?: defaultTransition
                performChange(
                    from = oldTopEntry,
                    to = newTopEntry,
                    isPush = isPush,
                    transition = localTransition
                )
            }
        }
    }

    private fun List<AnimatedStackEntryState>.filterVisible(): List<AnimatedStackEntryState> {
        val visibleEntries = mutableListOf<AnimatedStackEntryState>()

        for (entry in reversed()) {
            visibleEntries += entry
            if (!entry.entry.opaque) break
        }

        return visibleEntries
    }

    private fun performChange(
        from: AnimatedStackEntryState?,
        to: AnimatedStackEntryState?,
        isPush: Boolean,
        transition: StackTransition
    ) {
        val exitFrom = from != null && (!isPush || !to!!.entry.opaque)

        val transactionId = UUID.randomUUID()

        lateinit var transitionComposable: @Composable () -> Unit
        transitionComposable = {
            key(transactionId) {
                val fromAnimatable = from?.entry?.let { animatableFor(it) }
                val toAnimatable = to?.entry?.let { animatableFor(it) }

                val context = remember {
                    val completed = untrackedState { false }
                    StackTransitionContext(
                        fromAnimatable = fromAnimatable,
                        toAnimatable = toAnimatable,
                        isPush = isPush,
                        addToBlock = {
                            checkNotNull(to)
                            to.enter(from = from, isPush = isPush)
                        },
                        removeFromBlock = {
                            checkNotNull(from)
                            if (exitFrom) from.exit(to = to, isPush = isPush)
                        },
                        onCompleteBlock = {
                            check(!completed.value) {
                                "onComplete() must be called only once"
                            }
                            completed.value = true
                            activeTransitions -= transitionComposable
                            from?.onTransitionComplete()
                            to?.onTransitionComplete()
                        }
                    )
                }

                transition(context)
            }
        }
        activeTransitions += transitionComposable
    }

    @Stable
    internal inner class AnimatedStackEntryState(val entry: AnimatedStackEntry) {

        private var removeOnComplete = false

        @Composable
        fun content() {
            Box(
                modifier = Modifier.animatable(entry),
                children = entry.content
            )
        }

        fun enter(
            from: AnimatedStackEntryState?,
            isPush: Boolean
        ) {
            entry.isAnimating = true

            val oldToIndex = visibleEntries.indexOf(this)

            val fromIndex = visibleEntries.indexOf(from)
            val toIndex = if (fromIndex != -1) {
                if (isPush) fromIndex + 1 else fromIndex
            } else if (oldToIndex != -1) oldToIndex else visibleEntries.size

            if (oldToIndex != toIndex) {
                if (oldToIndex != -1) visibleEntries.removeAt(oldToIndex)
                visibleEntries.add(toIndex, this)
            }

            removeOnComplete = false
        }

        fun exit(
            to: AnimatedStackEntryState?,
            isPush: Boolean
        ) {
            entry.isAnimating = true
            removeOnComplete = true
        }

        fun onTransitionComplete() {
            entry.isAnimating = false
            if (removeOnComplete) {
                removeOnComplete = false
                visibleEntries.remove(this)
            }
        }

    }

}

@Stable
class AnimatedStackEntry(
    val key: Any?,
    val opaque: Boolean = false,
    val enterTransition: StackTransition? = null,
    val exitTransition: StackTransition? = null,
    val content: @Composable () -> Unit
) {
    var isAnimating by mutableStateOf(false)
        internal set
}
