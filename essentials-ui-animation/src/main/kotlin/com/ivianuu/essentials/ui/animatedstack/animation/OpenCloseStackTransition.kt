/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.animatedstack.animation

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.runtime.onActive
import androidx.compose.runtime.remember
import com.ivianuu.essentials.ui.animatedstack.StackTransition

fun OpenCloseStackTransition(): StackTransition = { context ->
    onActive { context.addTo() }

    val fromPropsKeys =
        if (context.fromAnimatable != null) remember { OpenClosePropKeys() } else null
    val toPropKeys = if (context.toAnimatable != null) remember { OpenClosePropKeys() } else null

    val transitionState = transition(
        definition = remember {
            transitionDefinition<Boolean> {
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
