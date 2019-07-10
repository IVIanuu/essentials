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

package com.ivianuu.essentials.ui.traveler

import com.ivianuu.director.*
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.director.common.changehandler.HorizontalChangeHandler
import com.ivianuu.director.common.changehandler.VerticalChangeHandler
import com.ivianuu.director.common.changehandler.defaultAnimationDuration

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

inline fun defaultNavOptionsOrElse(options: () -> ControllerNavOptions): ControllerNavOptions {
    val pushHandler = DirectorPlugins.defaultPushHandler
    val popHandler = DirectorPlugins.defaultPopHandler
    return if (pushHandler != null || popHandler != null)
        NavOptions().push(pushHandler).pop(popHandler)
    else options()
}

fun ControllerNavOptions.applyToTransaction(transaction: RouterTransaction): ControllerNavOptions =
    apply {
        transaction.pushChangeHandler(push())
        transaction.popChangeHandler(pop())
    }

fun ControllerNavOptions.handler(
    changeHandler: ControllerChangeHandler
): ControllerNavOptions = push(changeHandler).pop(changeHandler)

fun ControllerNavOptions.fade(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = fadePush(duration, removesFromViewOnPush)
    .fadePop(duration, removesFromViewOnPush)

fun ControllerNavOptions.fadePush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = push(FadeChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.fadePop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = pop(FadeChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.horizontal(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = horizontalPush(duration, removesFromViewOnPush)
    .horizontalPop(duration, removesFromViewOnPush)

fun ControllerNavOptions.horizontalPush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = push(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.horizontalPop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = pop(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.vertical(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = verticalPush(duration, removesFromViewOnPush)
    .verticalPop(duration, removesFromViewOnPush)

fun ControllerNavOptions.verticalPush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = push(VerticalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.verticalPop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = pop(VerticalChangeHandler(duration, removesFromViewOnPush))

fun ControllerNavOptions.dialog(): ControllerNavOptions =
    dialogPush().dialogPop()

fun ControllerNavOptions.dialogPush(): ControllerNavOptions =
    nonePush(false)

fun ControllerNavOptions.dialogPop(): ControllerNavOptions =
    nonePop(false)

fun ControllerNavOptions.nonePop(
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = pop(
    DefaultChangeHandler(removesFromViewOnPush)
)

fun ControllerNavOptions.nonePush(
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions = push(
    DefaultChangeHandler(removesFromViewOnPush)
)

fun ControllerNavOptions.none(
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerNavOptions =
    nonePop(removesFromViewOnPush).nonePush(removesFromViewOnPush)