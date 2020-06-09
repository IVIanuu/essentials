package com.ivianuu.essentials.ui.animatedstack

import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.frames.modelListOf
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.compose.setValue
import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.common.absorbPointer

@Composable
fun AnimatedStack(
    modifier: Modifier = Modifier,
    entries: List<AnimatedStackEntry>
) {
    val state = remember { AnimatedStackState() }
    state.defaultAnimation = DefaultStackAnimationAmbient.current
    state.setEntries(entries)
    StatefulStack(
        modifier = modifier,
        entries = state.statefulStackEntries
    )
}

private class AnimatedStackState {

    private var _entries = emptyList<AnimatedStackEntryState>()
    val statefulStackEntries = modelListOf<StatefulStackEntry>()

    var defaultAnimation = NoOpStackAnimation

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
        animation: StackAnimation?
    ) {
        val exitFrom = from != null && (!isPush || !to!!.entry.opaque)
        if (exitFrom) from!!.exit(to = to, isPush = isPush, animation = animation)
        to?.enter(from = from, isPush = isPush, animation = animation)
    }

    private inner class AnimatedStackEntryState(val entry: AnimatedStackEntry) {

        val statefulStackEntry = StatefulStackEntry(
            entry.opaque,
            entry.keepState
        ) {
            StackAnimation(
                modifier = Modifier.absorbPointer(entry.isAnimating),
                animation = currentAnimation ?: defaultAnimation,
                state = animationState,
                lastState = lastAnimationState,
                onComplete = onAnimationComplete,
                children = entry.content
            )
        }

        private val onAnimationComplete: (StackAnimation.State) -> Unit = { completedState ->
            entry.isAnimating = false
            lastAnimationState = completedState
            if (completedState == StackAnimation.State.ExitFromPush) {
                other?.onOtherAnimationComplete()
                other = null
            }

            if ((completedState == StackAnimation.State.ExitFromPush && !entry.keepState) ||
                completedState == StackAnimation.State.ExitFromPop
            ) {
                statefulStackEntries -= statefulStackEntry
            }
        }

        private var currentAnimation: StackAnimation? by mutableStateOf(entry.enterAnimation)
        private var animationState by mutableStateOf(StackAnimation.State.Init)
        private var lastAnimationState by mutableStateOf(StackAnimation.State.Init)

        private var other: AnimatedStackEntryState? by mutableStateOf(null)

        private fun onOtherAnimationComplete() {
            statefulStackEntry.opaque = entry.opaque
        }

        fun enter(
            from: AnimatedStackEntryState?,
            isPush: Boolean,
            animation: StackAnimation?
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

            lastAnimationState = animationState
            animationState = if (isPush) StackAnimation.State.EnterFromPush
            else StackAnimation.State.EnterFromPop
            this.currentAnimation = animation
        }

        fun exit(
            to: AnimatedStackEntryState?,
            isPush: Boolean,
            animation: StackAnimation?
        ) {
            entry.isAnimating = true
            statefulStackEntry.opaque = entry.opaque || !isPush
            if (isPush) other = to
            lastAnimationState = animationState
            animationState = if (isPush) StackAnimation.State.ExitFromPush
            else StackAnimation.State.ExitFromPop
            this.currentAnimation = animation
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
