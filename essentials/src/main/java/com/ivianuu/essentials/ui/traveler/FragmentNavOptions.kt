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

interface ChangeHandler

/**
 * Nav options for controllers
 */
class FragmentNavOptions {

    private var pushHandler: ChangeHandler? = null
    private var popHandler: ChangeHandler? = null

    fun push(): ChangeHandler? = pushHandler

    fun push(handler: ChangeHandler?): FragmentNavOptions = apply {
        pushHandler = handler
    }

    fun pop(): ChangeHandler? = popHandler

    fun pop(handler: ChangeHandler?): FragmentNavOptions = apply {
        popHandler = handler
    }

}

fun NavOptions(): FragmentNavOptions = FragmentNavOptions()

/*
fun FragmentNavOptions.applyToController(controller: Controller): FragmentNavOptions =
    apply {
        controller.pushChangeHandler(push())
        controller.popChangeHandler(pop())
}*/

fun FragmentNavOptions.handler(
    changeHandler: ChangeHandler
): FragmentNavOptions = push(changeHandler).pop(changeHandler)

fun FragmentNavOptions.fade(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = fadePush(duration, removesFromViewOnPush)
    .fadePop(duration, removesFromViewOnPush)

fun FragmentNavOptions.fadePush(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this//push(FadeChangeHandler(duration, removesFromViewOnPush))

fun FragmentNavOptions.fadePop(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this//pop(FadeChangeHandler(duration, removesFromViewOnPush))

fun FragmentNavOptions.horizontal(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = horizontalPush(duration, removesFromViewOnPush)
    .horizontalPop(duration, removesFromViewOnPush)

fun FragmentNavOptions.horizontalPush(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this//push(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun FragmentNavOptions.horizontalPop(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this//pop(HorizontalChangeHandler(duration, removesFromViewOnPush))

fun FragmentNavOptions.vertical(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = verticalPush(duration, removesFromViewOnPush)
    .verticalPop(duration, removesFromViewOnPush)

fun FragmentNavOptions.verticalPush(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this//push(VerticalChangeHandler(duration, removesFromViewOnPush))

fun FragmentNavOptions.verticalPop(
    duration: Long = 300,
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this//pop(VerticalChangeHandler(duration, removesFromViewOnPush))

fun FragmentNavOptions.dialog(): FragmentNavOptions =
    dialogPush().dialogPop()

fun FragmentNavOptions.dialogPush(): FragmentNavOptions =
    nonePush(false)

fun FragmentNavOptions.dialogPop(): FragmentNavOptions =
    nonePop(false)

fun FragmentNavOptions.nonePop(
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this/*pop(
    DefaultChangeHandler(removesFromViewOnPush)
)*/

fun FragmentNavOptions.nonePush(
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions = this/*push(
    DefaultChangeHandler(removesFromViewOnPush)
)*/

fun FragmentNavOptions.none(
    removesFromViewOnPush: Boolean = true
): FragmentNavOptions =
    nonePop(removesFromViewOnPush).nonePush(removesFromViewOnPush)