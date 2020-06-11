package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.onPreCommit
import androidx.compose.remember
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.common.transition

fun OpenCloseStackTransition(

): StackTransition = { context ->
    onPreCommit(true) { context.addTo() }

    val fromPropsKeys =
        if (context.fromAnimatable != null) remember { OpenClosePropKeys() } else null
    val toPropKeys = if (context.toAnimatable != null) remember { OpenClosePropKeys() } else null

    val transitionState = transition(
        definition = remember {
            transitionDefinition {
                state(false) {
                    if (context.isPush) {
                        /*
                         <alpha
        android:fromAlpha="1"
        android:toAlpha="0.0"
        android:fillEnabled="true"
        android:fillBefore="true"
        android:fillAfter="true"
        android:interpolator="@android:anim/linear_interpolator"
        android:startOffset="35"
        android:duration="50"/>
    <scale
        android:fromXScale="1"
        android:toXScale="1.15"
        android:fromYScale="1"
        android:toYScale="1.15"
        android:pivotX="50%"
        android:pivotY="50%"
        android:fillEnabled="true"
        android:fillBefore="true"
        android:fillAfter="true"
        android:interpolator="@anim/fragment_fast_out_extra_slow_in"
        android:duration="300"/>
                         */
                        if (fromPropsKeys != null) {
                            set(fromPropsKeys.alpha, 1f)
                            set(fromPropsKeys.scale, 1f)
                        }
                        if (toPropKeys != null) {
                            set(toPropKeys.alpha, 1f)
                            set(toPropKeys.scale, 1f)
                        }
                    } else {
                        if (fromPropsKeys != null) {
                            set(fromPropsKeys.alpha, 1f)
                            set(fromPropsKeys.scale, 1f)
                        }
                        if (toPropKeys != null) {
                            set(toPropKeys.alpha, 1f)
                            set(toPropKeys.scale, 1f)
                        }
                    }
                }
                state(true) {
                    if (context.isPush) {

                    } else {

                    }
                }
            }
        },
        initState = false,
        toState = true,
        onStateChangeFinished = {
            context.removeFrom()
            context.onComplete()
        }
    )

}

private class OpenClosePropKeys {
    val alpha = FloatPropKey()
    val scale = FloatPropKey()
}
