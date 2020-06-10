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
import androidx.ui.core.boundsInRoot
import androidx.ui.core.onPositioned
import androidx.ui.foundation.Box
import androidx.ui.unit.PxBounds
import com.ivianuu.essentials.ui.animatable.AnimatableElementsAmbient
import com.ivianuu.essentials.ui.animatable.AnimatableElementsRoot
import com.ivianuu.essentials.ui.animatable.animatableElement
import com.ivianuu.essentials.ui.common.untrackedState
import java.util.UUID

@Composable
fun AnimatedStack(
    modifier: Modifier = Modifier,
    entries: List<AnimatedStackEntry>
) {
    AnimatableElementsRoot {
        val state = remember { AnimatedStackState() }
        state.defaultAnimation = DefaultStackAnimationAmbient.current
        state.setEntries(entries)
        state.activeAnimations.forEach { it() }
        StatefulStack(
            modifier = modifier
                .onPositioned { state.containerBounds = it.boundsInRoot },
            entries = state.statefulStackEntries
        )
    }
}

private class AnimatedStackState {

    private var _entries = emptyList<AnimatedStackEntryState>()
    val statefulStackEntries = modelListOf<StatefulStackEntry>()

    var defaultAnimation = NoOpStackAnimation
    var containerBounds: PxBounds? = null

    val activeAnimations = modelListOf<@Composable () -> Unit>()

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
                    val localAnimation = entry.entry.exitAnimation
                        ?: defaultAnimation
                    performChange(
                        from = entry,
                        to = null,
                        isPush = false,
                        animation = localAnimation
                    )
                }

            // Add any new entries to the stack from bottom to top
            newVisibleEntries
                .dropLast(if (replacingTopEntries) 1 else 0)
                .filterNot { it in oldVisibleEntries }
                .forEachIndexed { i, entry ->
                    val localAnimation =
                        entry.entry.enterAnimation ?: defaultAnimation
                    performChange(
                        from = newVisibleEntries.getOrNull(i - 1),
                        to = entry,
                        isPush = true,
                        animation = localAnimation
                    )
                }

            val isPush = newTopEntry !in oldEntries

            // Replace the old visible top with the new one
            if (replacingTopEntries) {
                val localAnimation =
                    if (isPush) newTopEntry?.entry?.enterAnimation ?: defaultAnimation
                    else oldTopEntry?.entry?.exitAnimation ?: defaultAnimation
                performChange(
                    from = oldTopEntry,
                    to = newTopEntry,
                    isPush = isPush,
                    animation = localAnimation
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
        animation: StackAnimation
    ) {
        val exitFrom = from != null && (!isPush || !to!!.entry.opaque)

        val transactionId = UUID.randomUUID()

        lateinit var animationComposable: @Composable () -> Unit
        animationComposable = {
            key(transactionId) {
                val completed = untrackedState { false }

                val addTo: () -> Unit = remember {
                    {
                        checkNotNull(to)
                        to.enter(from = from, isPush = isPush)
                    }
                }

                val removeFrom: () -> Unit = remember {
                    {
                        checkNotNull(from)
                        if (exitFrom) from.exit(to = to, isPush = isPush)
                    }
                }

                val onComplete: () -> Unit = remember {
                    {
                        check(!completed.value) {
                            "onComplete() must be called only once"
                        }
                        completed.value = true
                        activeAnimations -= animationComposable
                        from?.onAnimationComplete()
                        to?.onAnimationComplete()
                    }
                }

                val elements = AnimatableElementsAmbient.current


                val fromElement = from?.entry?.let { elements.getElement(it) }?.value
                val toElement = to?.entry?.let { elements.getElement(it) }?.value

                val context = remember(containerBounds) {
                    StackAnimationContext(
                        fromElement,
                        toElement,
                        containerBounds,
                        isPush,
                        addTo,
                        removeFrom,
                        onComplete
                    )
                }

                animation(context)
            }
        }
        activeAnimations += animationComposable
    }

    private inner class AnimatedStackEntryState(val entry: AnimatedStackEntry) {

        val statefulStackEntry = StatefulStackEntry(
            entry.opaque,
            entry.keepState
        ) {
            Box(
                modifier = Modifier.animatableElement(entry),
                children = entry.content
            )
        }

        private var removeOnComplete = false

        fun enter(
            from: AnimatedStackEntryState?,
            isPush: Boolean
        ) {
            statefulStackEntry.opaque = entry.opaque || isPush
            entry.isAnimating = true

            val fromIndex = statefulStackEntries.indexOf(from?.statefulStackEntry)
            val toIndex = if (fromIndex != -1) {
                if (isPush) fromIndex + 1 else fromIndex
            } else statefulStackEntries.size

            val oldToIndex = statefulStackEntries.indexOf(statefulStackEntry)

            if (oldToIndex == -1) {
                statefulStackEntries.add(toIndex, statefulStackEntry)
            } else if (oldToIndex != toIndex) {
                statefulStackEntries.removeAt(oldToIndex)
                statefulStackEntries.add(toIndex, statefulStackEntry)
            }

            removeOnComplete = false
        }

        fun exit(
            to: AnimatedStackEntryState?,
            isPush: Boolean
        ) {
            entry.isAnimating = true
            statefulStackEntry.opaque = entry.opaque || !isPush
            removeOnComplete = true
        }

        fun onAnimationComplete() {
            entry.isAnimating = false
            if (removeOnComplete) {
                removeOnComplete = false
                statefulStackEntries.remove(statefulStackEntry)
            }
        }

    }

}

@Stable
class AnimatedStackEntry(
    val opaque: Boolean = false,
    val keepState: Boolean = false,
    val enterAnimation: StackAnimation? = null,
    val exitAnimation: StackAnimation? = null,
    val content: @Composable () -> Unit
) {
    var isAnimating by mutableStateOf(false)
        internal set
}
