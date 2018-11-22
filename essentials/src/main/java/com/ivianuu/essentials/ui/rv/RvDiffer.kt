package com.ivianuu.essentials.ui.rv

/**

/**
 * An adaptation of Google's [androidx.recyclerview.widget.AsyncListDiffer]
 * that adds support for payloads in changes.
 *
 *
 * Also adds support for canceling an in progress diff, and makes everything thread safe.
*/
internal class AsyncEpoxyDiffer(
private val executor: Executor,
private val resultCallback: (DiffResult) -> Unit,
private val diffCallback: ItemCallback<RvModel<*>>
) {

private val generationTracker = GenerationTracker()

private var list: List<RvModel<*>>? = null

var currentList = Collections.emptyList()
private set

val isDiffInProgress: Boolean
get() = generationTracker.hasUnfinishedGeneration()

private val lock = ReentrantLock()

/**
 * Prevents any ongoing diff from dispatching results. Returns true if there was an ongoing
 * diff to cancel, false otherwise.
*/
fun cancelDiff() = generationTracker.finishMaxGeneration()

/**
 * Set the current list without performing any diffing. Cancels any diff in progress.
 *
 *
 * This can be used if you notified a change to the adapter manually and need this list to be
 * synced.
*/
@AnyThread
@Synchronized
fun forceListOverride(newList: List<RvModel<*>>): Boolean {
// We need to make sure that generation changes and list updates are synchronized
val interruptedDiff = cancelDiff()
val generation = generationTracker.incrementAndGetNextScheduled()
tryLatchList(newList, generation)
return interruptedDiff
}

/**
 * Set a new List representing your latest data.
 *
 *
 * A diff will be computed between this list and the last list set. If this has not previously
 * been called then an empty list is used as the previous list.
 *
 *
 * The diff computation will be done on the thread given by the handler in the constructor.
 * When the diff is done it will be applied (dispatched to the result callback),
 * and the new List will be swapped in.
*/
@AnyThread
fun submitList(newList: List<RvModel<*>>?) {
val runGeneration: Int
val previousList: List<RvModel<*>>?

synchronized(this) {
// Incrementing generation means any currently-running diffs are discarded when they finish
// We synchronize to guarantee list object and generation number are in sync
runGeneration = generationTracker.incrementAndGetNextScheduled()
previousList = list
}

if (newList === previousList) {
// nothing to do
onRunCompleted(runGeneration, newList, DiffResult.noOp(previousList))
return
}

if (newList == null || newList.isEmpty()) {
// fast simple clear all
var result: DiffResult? = null
if (previousList != null && !previousList.isEmpty()) {
result = DiffResult.clear(previousList)
}
onRunCompleted(runGeneration, null, result)
return
}

if (previousList == null || previousList.isEmpty()) {
// fast simple first insert
onRunCompleted(runGeneration, newList, DiffResult.inserted(newList))
return
}

val wrappedCallback = DiffCallback(previousList, newList, diffCallback)

executor.execute(Runnable {
val result = DiffUtil.calculateDiff(wrappedCallback)
onRunCompleted(runGeneration, newList, DiffResult.diff(previousList, newList, result))
})
}

private fun onRunCompleted(
runGeneration: Int,
newList: List<RvModel<*>>?,
result: DiffResult?
) {

// We use an asynchronous handler so that the Runnable can be posted directly back to the main
// thread without waiting on view invalidation synchronization.
MainThreadExecutor.ASYNC_INSTANCE.execute(Runnable {
val dispatchResult = tryLatchList(newList, runGeneration)
if (result != null && dispatchResult) {
resultCallack.onResult(result)
}
})
}

/**
 * Marks the generation as done, and updates the list if the generation is the most recent.
*/
@AnyThread
@Synchronized
private fun tryLatchList(
newList: List<RvModel<*>>?,
runGeneration: Int
): Boolean {
if (generationTracker.finishGeneration(runGeneration)) {
list = newList

if (newList == null) {
currentList = Collections.emptyList()
} else {
currentList = Collections.unmodifiableList(newList)
}

return true
}

return false
}

/**
 * The concept of a "generation" is used to associate a diff result with a point in time when
 * it was created. This allows us to handle list updates concurrently, and ignore outdated diffs.
 *
 *
 * We track the highest start generation, and the highest finished generation, and these must
 * be kept in sync, so all access to this class is synchronized.
 *
 *
 * The general synchronization strategy for this class is that when a generation number
 * is queried that action must be synchronized with accessing the current list, so that the
 * generation number is synced with the list state at the time it was created.
*/
private class GenerationTracker {

// Max generation of currently scheduled runnable
@Volatile private var maxScheduledGeneration: Int = 0
@Volatile private var maxFinishedGeneration: Int = 0

@Synchronized
internal fun incrementAndGetNextScheduled(): Int {
return ++maxScheduledGeneration
}

@Synchronized
internal fun finishMaxGeneration(): Boolean {
val isInterrupting = hasUnfinishedGeneration()
maxFinishedGeneration = maxScheduledGeneration
return isInterrupting
}

@Synchronized
internal fun hasUnfinishedGeneration(): Boolean {
return maxScheduledGeneration > maxFinishedGeneration
}

@Synchronized
internal fun finishGeneration(runGeneration: Int): Boolean {
val isLatestGeneration =
maxScheduledGeneration == runGeneration && runGeneration > maxFinishedGeneration

if (isLatestGeneration) {
maxFinishedGeneration = runGeneration
}

return isLatestGeneration
}
}

private class DiffCallback internal constructor(
internal val oldList: List<RvModel<*>>,
internal val newList: List<RvModel<*>>,
private val diffCallback: ItemCallback<RvModel<*>>
) : DiffUtil.Callback() {

val oldListSize: Int
get() = oldList.size

val newListSize: Int
get() = newList.size

override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
return diffCallback.areItemsTheSame(
oldList[oldItemPosition],
newList[newItemPosition]
)
}

override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
return diffCallback.areContentsTheSame(
oldList[oldItemPosition],
newList[newItemPosition]
)
}

fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
return diffCallback.getChangePayload(
oldList[oldItemPosition],
newList[newItemPosition]
)
}
}
}*/