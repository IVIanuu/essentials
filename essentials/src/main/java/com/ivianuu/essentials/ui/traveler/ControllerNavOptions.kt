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

import com.ivianuu.director.ControllerChangeHandler
import com.ivianuu.director.DirectorPlugins
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.SimpleSwapChangeHandler
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.director.common.changehandler.HorizontalChangeHandler
import com.ivianuu.director.common.changehandler.VerticalChangeHandler
import com.ivianuu.director.common.changehandler.defaultAnimationDuration
import com.ivianuu.director.popChangeHandler
import com.ivianuu.director.pushChangeHandler

/**
 * Nav options for controllers
 */
class ControllerNavOptions {

    private var pushHandler: ControllerChangeHandler? = null
    private var popHandler: ControllerChangeHandler? = null

    fun push(): ControllerChangeHandler? = pushHandler

    fun push(handler: ControllerChangeHandler?): ControllerNavOptions = apply {
        pushHandler = handler
    }

    fun pop(): ControllerChangeHandler? = popHandler

    fun pop(handler: ControllerChangeHandler?): ControllerNavOptions = apply {
        popHandler = handler
    }

}

fun NavOptions(): ControllerNavOptions = ControllerNavOptions()

fun ControllerNavOptions.applyToTransaction(transaction: RouterTransaction): ControllerNavOptions = apply {
    transaction.pushChangeHandler(push())
    transaction.popChangeHandler(pop())
}

fun ControllerNavOptions.handler(
    changeHandler: ControllerChangeHandler
): ControllerNavOptions = push(changeHandler).pop(changeHandler)

fun ControllerNavOptions.fade(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = fadePush(duration, removesFromViewOnPush)
    .fadePop(duration, removesFromViewOnPush)

fun ControllerNavOptions.fadePush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = push(FadeChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.fadePop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = pop(FadeChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.horizontal(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = horizontalPush(duration, removesFromViewOnPush)
    .horizontalPop(duration, removesFromViewOnPush)

fun ControllerNavOptions.horizontalPush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = push(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.horizontalPop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = pop(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.vertical(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = verticalPush(duration, removesFromViewOnPush)
    .verticalPop(duration, removesFromViewOnPush)

fun ControllerNavOptions.verticalPush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = push(VerticalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.verticalPop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = pop(VerticalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.swap(
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = swapPush(removesFromViewOnPush).swapPop(removesFromViewOnPush)

fun ControllerNavOptions.swapPush(
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = push(SimpleSwapChangeHandler(removesFromViewOnPush))

fun ControllerNavOptions.swapPop(
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = pop(SimpleSwapChangeHandler(removesFromViewOnPush))

fun ControllerNavOptions.dialog(): ControllerNavOptions =
    dialogPush().dialogPop()

fun ControllerNavOptions.dialogPush(): ControllerNavOptions =
    swapPush(true)

fun ControllerNavOptions.dialogPop(): ControllerNavOptions =
    swapPop(true)

fun ControllerNavOptions.nonePop(): ControllerNavOptions = pop(null)

fun ControllerNavOptions.nonePush(): ControllerNavOptions = push(null)

fun ControllerNavOptions.none(): ControllerNavOptions = nonePop().nonePush()