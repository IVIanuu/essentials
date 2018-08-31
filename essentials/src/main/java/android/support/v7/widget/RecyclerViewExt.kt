package android.support.v7.widget

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable

@SuppressLint("VisibleForTests")
fun RecyclerView.initializeFastScroller(
    verticalThumbDrawable: StateListDrawable,
    verticalTrackDrawable: Drawable,
    horizontalThumbDrawable: StateListDrawable,
    horizontalTrackDrawable: Drawable
) {
    initFastScroller(
        verticalThumbDrawable, verticalTrackDrawable,
        horizontalThumbDrawable, horizontalTrackDrawable
    )
}