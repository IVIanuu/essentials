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
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.director.common.changehandler.HorizontalChangeHandler
import com.ivianuu.director.common.changehandler.VerticalChangeHandler
import com.ivianuu.director.common.changehandler.defaultAnimationDuration

/**
 * Nav options for controllers
 */
class ControllerNavOptions {

    private var pushHandler: ChangeHandler? = null
    private var popHandler: ChangeHandler? = null

    fun push(): ChangeHandler? = pushHandler

    fun push(handler: ChangeHandler?): ControllerNavOptions = apply {
        pushHandler = handler
    }

    fun pop(): ChangeHandler? = popHandler

    fun pop(handler: ChangeHandler?): ControllerNavOptions = apply {
        popHandler = handler
    }

}

fun NavOptions(): ControllerNavOptions = ControllerNavOptions()

fun ControllerNavOptions.applyToController(controller: Controller): ControllerNavOptions =
    apply {
        controller.pushChangeHandler(push())
        controller.popChangeHandler(pop())
}

fun ControllerNavOptions.handler(
    changeHandler: ChangeHandler
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

fun ControllerNavOptions.dialog(): ControllerNavOptions =
    dialogPush().dialogPop()

fun ControllerNavOptions.dialogPush(): ControllerNavOptions =
    nonePush(false)

fun ControllerNavOptions.dialogPop(): ControllerNavOptions =
    nonePop(false)

fun ControllerNavOptions.nonePop(removesFromViewOnPush: Boolean = true): ControllerNavOptions = pop(
    DefaultChangeHandler(removesFromViewOnPush)
)

fun ControllerNavOptions.nonePush(
    removesFromViewOnPush: Boolean = true
): ControllerNavOptions = push(
    DefaultChangeHandler(removesFromViewOnPush)
)

fun ControllerNavOptions.none(removesFromViewOnPush: Boolean = true): ControllerNavOptions =
    nonePop(removesFromViewOnPush).nonePush(removesFromViewOnPush)