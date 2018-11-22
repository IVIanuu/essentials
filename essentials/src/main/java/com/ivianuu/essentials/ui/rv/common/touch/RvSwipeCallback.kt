package com.ivianuu.essentials.ui.rv.common.touch

import android.graphics.Canvas
import android.view.View
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.ivianuu.essentials.ui.rv.RvModel

/**
 * For use with [EpoxyModelTouchCallback]
 */
interface RvSwipeCallback<T : RvModel<*>> : BaseRvTouchCallback<T> {

    /**
     * Called when the view switches from an idle state to a swiped state, as the user begins a swipe
     * interaction with it. You can use this callback to modify the view to indicate it is being
     * swiped.
     */
    fun onSwipeStarted(model: T, itemView: View, adapterPosition: Int)

    /**
     * Once a view has begun swiping with [.onSwipeStarted] it will
     * receive this callback as the swipe distance changes. This can be called multiple times as the
     * swipe interaction progresses.
     */
    fun onSwipeProgressChanged(
        model: T, itemView: View, swipeProgress: Float,
        canvas: Canvas
    )

    /**
     * Called when the user has released their touch on the view. If the displacement passed the swipe
     * threshold then [.onSwipeCompleted] will be called after this
     * and the view will be animated off screen. Otherwise the view will animate back to its original
     * position.
     */
    fun onSwipeReleased(model: T, itemView: View)

    /**
     * Called after [.onSwipeReleased] if the swipe surpassed the threshold to
     * be considered a full swipe. The view will now be animated off screen.
     */
    fun onSwipeCompleted(model: T, itemView: View, position: Int, direction: Int)
}