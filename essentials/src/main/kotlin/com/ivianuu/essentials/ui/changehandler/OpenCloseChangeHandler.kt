/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.changehandler

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.ivianuu.compose.ComponentChangeHandler
import com.ivianuu.compose.common.changehandler.AnimatorChangeHandler
import com.ivianuu.kommon.core.animation.animatorSetOf

fun OpenCloseChangeHandler(
    duration: Long = AnimatorChangeHandler.NO_DURATION
): ComponentChangeHandler {
    return AnimatorChangeHandler(duration) { changeData ->
        val animator = AnimatorSet()
        if (changeData.isPush) {
            if (changeData.from != null) {
                animator.play(
                    createOpenCloseAnimator(
                        changeData.from!!,
                        1.0f, 0.975f, 1f, 0f
                    )
                )
            }
            if (changeData.to != null) {
                animator.play(
                    createOpenCloseAnimator(
                        changeData.to!!,
                        1.125f, 1.0f, 0f, 1f
                    )
                )
            }
        } else {
            if (changeData.from != null) {
                animator.play(
                    createOpenCloseAnimator(
                        changeData.from!!,
                        1.0f, 1.075f, 1f, 0f
                    )
                )
            }
            if (changeData.to != null) {
                animator.play(
                    createOpenCloseAnimator(
                        changeData.to!!,
                        0.975f, 1.0f, 0f, 1f
                    )
                )
            }
        }

        return@AnimatorChangeHandler animator
    }
}

private val DECELERATE_QUINT = DecelerateInterpolator(2.5f)
private val DECELERATE_CUBIC = DecelerateInterpolator(1.5f)

private fun createOpenCloseAnimator(
    view: View,
    startScale: Float,
    endScale: Float,
    startAlpha: Float,
    endAlpha: Float
) = animatorSetOf(
    ObjectAnimator.ofFloat(
        view,
        View.SCALE_X,
        startScale, endScale
    ).apply { interpolator = DECELERATE_QUINT },
    ObjectAnimator.ofFloat(
        view,
        View.SCALE_Y,
        startScale, endScale
    ).apply { interpolator = DECELERATE_QUINT },
    ObjectAnimator.ofFloat(
        view,
        View.ALPHA,
        startAlpha, endAlpha
    ).apply { interpolator = DECELERATE_CUBIC }
)