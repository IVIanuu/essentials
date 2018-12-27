/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.ui.traveler

import com.ivianuu.director.*
import com.ivianuu.director.common.changehandler.*

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ControllerNavOptions {

    private var pushHandler: ControllerChangeHandler? = null
    private var popHandler: ControllerChangeHandler? = null

    fun push() = pushHandler

    fun push(handler: ControllerChangeHandler?) = apply {
        pushHandler = handler
    }

    fun pop() = popHandler

    fun pop(handler: ControllerChangeHandler?) = apply {
        popHandler = handler
    }

}

fun NavOptions() = ControllerNavOptions()

fun ControllerNavOptions.applyToTransaction(transaction: RouterTransaction) = apply {
    transaction.pushChangeHandler(push())
    transaction.popChangeHandler(pop())
}

fun ControllerNavOptions.handler(
    changeHandler: ControllerChangeHandler
) = push(changeHandler).pop(changeHandler)

fun ControllerNavOptions.fade(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = fadePush(duration, removesFromViewOnPush)
    .fadePop(duration, removesFromViewOnPush)

fun ControllerNavOptions.fadePush(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = push(FadeChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.fadePop(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = pop(FadeChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.horizontal(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = horizontalPush(duration, removesFromViewOnPush)
    .horizontalPop(duration, removesFromViewOnPush)

fun ControllerNavOptions.horizontalPush(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = push(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.horizontalPop(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = pop(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.vertical(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = verticalPush(duration, removesFromViewOnPush)
    .verticalPop(duration, removesFromViewOnPush)

fun ControllerNavOptions.verticalPush(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = push(VerticalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.verticalPop(
    duration: Long = AnimatorChangeHandler.DEFAULT_ANIMATION_DURATION,
    removesFromViewOnPush: Boolean = true
) = pop(VerticalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.auto() =
    autoPush().autoPop()

fun ControllerNavOptions.autoPush() =
    push(AutoTransitionChangeHandler())

fun ControllerNavOptions.autoPop() = pop(AutoTransitionChangeHandler())

fun ControllerNavOptions.dialog() =
    dialogPush().dialogPop()

fun ControllerNavOptions.dialogPush() = push(
    SimpleSwapChangeHandler(false)
)

fun ControllerNavOptions.dialogPop() = pop(
    SimpleSwapChangeHandler(false)
)