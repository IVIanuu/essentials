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

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.ivianuu.director.ChangeData
import com.ivianuu.director.DirectorPlugins
import com.ivianuu.director.common.changehandler.AnimatorChangeHandler
import com.ivianuu.director.common.changehandler.defaultAnimationDuration
import com.ivianuu.director.defaultRemovesFromViewOnPush
import com.ivianuu.essentials.ui.navigation.director.ControllerRoute
import com.ivianuu.kommon.core.animation.animatorSetOf

class OpenCloseChangeHandler(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
) : AnimatorChangeHandler(duration, removesFromViewOnPush) {

    override fun getAnimator(changeData: ChangeData): Animator {
        val animator = animatorSetOf()

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

        return animator
    }

    private companion object {

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
    }

    override fun copy() = OpenCloseChangeHandler(duration, removesFromViewOnPush)
}

fun ControllerRoute.Options.openClose(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = openClosePush(duration, removesFromViewOnPush)
    .openClosePop(duration, removesFromViewOnPush)

fun ControllerRoute.Options.openClosePush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = pushHandler(
    OpenCloseChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerRoute.Options.openClosePop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = popHandler(
    OpenCloseChangeHandler(
        duration,
        removesFromViewOnPush
    )
)
