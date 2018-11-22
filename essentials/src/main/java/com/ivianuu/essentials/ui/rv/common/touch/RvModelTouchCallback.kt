package com.ivianuu.essentials.ui.rv.common.touch

/**
/**
 * A wrapper around [androidx.recyclerview.widget.ItemTouchHelper.Callback] to enable
 * easier touch support when working with Rv models.
 *
 *
 * For simplicity you can use [RvTouchHelper] to set up touch handling via this class for
 * you instead of using this class directly. However, you may choose to use this class directly with
 * your own [ItemTouchHelper] if you need extra flexibility or customization.
*/
abstract class RvModelTouchCallback<T : RvModel<*>>(
private val controller: RvController,
private val targetModelClass: Class<T>
) : ItemTouchHelper.Callback(), RvDragCallback<T>, RvSwipeCallback<T> {

private var holderBeingDragged: RvViewHolder? = null
private var holderBeingSwiped: RvViewHolder? = null

override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
viewHolder as RvViewHolder
val model = viewHolder.model

// If multiple touch callbacks are registered on the recyclerview (to support combinations of
// dragging and dropping) then we won't want to enable anything if another
// callback has a view actively selected.
val isOtherCallbackActive = (holderBeingDragged == null
&& holderBeingSwiped == null
&& recyclerViewHasSelection(recyclerView))

return if (!isOtherCallbackActive && isTouchableModel(model)) {
getMovementFlagsForModel(model as T, viewHolder.adapterPosition)
} else {
0
}
}

override fun canDropOver(
recyclerView: RecyclerView, current: RecyclerView.ViewHolder,
target: RecyclerView.ViewHolder
): Boolean {
target as RvViewHolder
// By default we don't allow dropping on a model that isn't a drag target
return isTouchableModel(target.model!!)
}

protected fun isTouchableModel(model: RvModel<*>) =
targetModelClass.isInstance(model)

override fun onMove(
recyclerView: RecyclerView, viewHolder: RvViewHolder,
target: RvViewHolder
): Boolean {

if (controller == null) {
throw IllegalStateException(
"A controller must be provided in the constructor if dragging is enabled"
)
}

val fromPosition = viewHolder.adapterPosition
val toPosition = target.adapterPosition
controller.moveModel(fromPosition, toPosition)

val model = viewHolder.model
if (!isTouchableModel(model)) {
throw IllegalStateException(
"A model was dragged that is not a valid target: " + model.javaClass
)
}


onModelMoved(fromPosition, toPosition, model as T, viewHolder.itemView)
return true
}

fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: T, itemView: View) {

}

override fun onSwiped(viewHolder: RvViewHolder, direction: Int) {
val model = viewHolder.model
val view = viewHolder.itemView
val position = viewHolder.adapterPosition

if (!isTouchableModel(model)) {
throw IllegalStateException(
"A model was swiped that is not a valid target: " + model.javaClass
)
}


onSwipeCompleted(model as T, view, position, direction)
}

fun onSwipeCompleted(model: T, itemView: View, position: Int, direction: Int) {

}

override fun onSelectedChanged(@Nullable viewHolder: RvViewHolder?, actionState: Int) {
super.onSelectedChanged(viewHolder, actionState)

if (viewHolder != null) {
val model = viewHolder.model
if (!isTouchableModel(model)) {
throw IllegalStateException(
"A model was selected that is not a valid target: " + model.javaClass
)
}

markRecyclerViewHasSelection(viewHolder.itemView.parent as RecyclerView)

if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
holderBeingSwiped = viewHolder

onSwipeStarted(model as T, viewHolder.itemView, viewHolder.adapterPosition)
} else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
holderBeingDragged = viewHolder

onDragStarted(model as T, viewHolder.itemView, viewHolder.adapterPosition)
}
} else if (holderBeingDragged != null) {

onDragReleased(holderBeingDragged!!.model as T, holderBeingDragged!!.itemView)
holderBeingDragged = null
} else if (holderBeingSwiped != null) {

onSwipeReleased(holderBeingSwiped!!.model as T, holderBeingSwiped!!.itemView)
holderBeingSwiped = null
}
}

private fun markRecyclerViewHasSelection(recyclerView: RecyclerView) {
recyclerView.setTag(R.id.epoxy_touch_helper_selection_status, java.lang.Boolean.TRUE)
}

private fun recyclerViewHasSelection(recyclerView: RecyclerView): Boolean {
return recyclerView.getTag(R.id.epoxy_touch_helper_selection_status) != null
}

private fun clearRecyclerViewSelectionMarker(recyclerView: RecyclerView) {
recyclerView.setTag(R.id.epoxy_touch_helper_selection_status, null)
}

fun onSwipeStarted(model: T, itemView: View, adapterPosition: Int) {

}

fun onSwipeReleased(model: T, itemView: View) {

}

fun onDragStarted(model: T, itemView: View, adapterPosition: Int) {

}

fun onDragReleased(model: T, itemView: View) {

}

override fun clearView(recyclerView: RecyclerView?, viewHolder: RvViewHolder) {
super.clearView(recyclerView, viewHolder)

clearView(viewHolder.model as T, viewHolder.itemView)

// If multiple touch helpers are in use, one touch helper can pick up buffered touch inputs
// immediately after another touch event finishes. This leads to things like a view being
// selected for drag when another view finishes its swipe off animation. To prevent that we
// keep the recyclerview marked as having an active selection for a brief period after a
// touch event ends.
recyclerView!!.postDelayed(
{ clearRecyclerViewSelectionMarker(recyclerView) },
TOUCH_DEBOUNCE_MILLIS.toLong()
)
}

fun clearView(model: T, itemView: View) {

}

override fun onChildDraw(
c: Canvas, recyclerView: RecyclerView, viewHolder: RvViewHolder,
dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
) {
super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

if (holderBeingSwiped == null) {
return
}

val model = viewHolder.model
if (!isTouchableModel(model)) {
throw IllegalStateException(
"A model was selected that is not a valid target: " + model.javaClass
)
}

val itemView = viewHolder.itemView

val swipeProgress: Float
if (Math.abs(dX) > Math.abs(dY)) {
swipeProgress = dX / itemView.width
} else {
swipeProgress = dY / itemView.height
}

// Clamp to 1/-1 in the case of side padding where the view can be swiped extra
val clampedProgress = Math.max(-1f, Math.min(1f, swipeProgress))


onSwipeProgressChanged(model as T, itemView, clampedProgress, c)
}

override fun onSwipeProgressChanged(
model: T, itemView: View, swipeProgress: Float,
canvas: Canvas
) {

}

companion object {

private val TOUCH_DEBOUNCE_MILLIS = 300
}
}*/