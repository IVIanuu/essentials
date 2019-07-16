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
import com.ivianuu.director.ChangeData
import com.ivianuu.director.DirectorPlugins
import com.ivianuu.director.common.changehandler.AnimatorChangeHandler
import com.ivianuu.director.common.changehandler.defaultAnimationDuration
import com.ivianuu.director.defaultRemovesFromViewOnPush
import com.ivianuu.essentials.ui.traveler.ControllerNavOptions
import com.ivianuu.kommon.core.animation.animatorSetOf

class VerticalFadeChangeHandler(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
) : AnimatorChangeHandler(duration, removesFromViewOnPush) {

    override fun getAnimator(changeData: ChangeData): Animator {
        val (_, from, to, isPush) = changeData
        val animator = animatorSetOf()

        if (isPush && to != null) {
            animator.play(ObjectAnimator.ofFloat(to, View.ALPHA, 0f, 1f))
            animator.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, to.height * 0.3f, 0f))
        } else if (!isPush && from != null) {
            animator.play(ObjectAnimator.ofFloat(from, View.ALPHA, 1f, 0f))
            animator.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_Y, 0f, from.height * 0.3f))
        }

        return animator
    }

    override fun copy() = VerticalFadeChangeHandler(duration, removesFromViewOnPush)

}

fun ControllerNavOptions.verticalFade(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = verticalFadePush(duration, removesFromViewOnPush)
    .verticalFadePop(duration, removesFromViewOnPush)

fun ControllerNavOptions.verticalFadePush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = push(
    VerticalFadeChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerNavOptions.verticalFadePop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = pop(
    VerticalFadeChangeHandler(
        duration,
        removesFromViewOnPush
    )
)