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

package com.ivianuu.essentials.ui.traveler.anim

/**
 * Nav options for controllers
 */
class FragmentNavOptions {

    private var pushTransition: FragmentTransition? = null
    private var popTransition: FragmentTransition? = null

    fun push(): FragmentTransition? = pushTransition

    fun push(transition: FragmentTransition?): FragmentNavOptions = apply {
        pushTransition = transition
    }

    fun pop(): FragmentTransition? = popTransition

    fun pop(transition: FragmentTransition?): FragmentNavOptions = apply {
        popTransition = transition
    }

}

fun NavOptions(): FragmentNavOptions =
    FragmentNavOptions()

fun FragmentNavOptions.transition(
    changeHandler: FragmentTransition
): FragmentNavOptions = push(changeHandler).pop(changeHandler)

fun FragmentNavOptions.fade(
    duration: Long = -1L
): FragmentNavOptions = fadePush(duration)
    .fadePop(duration)

fun FragmentNavOptions.fadePush(
    duration: Long = -1L
): FragmentNavOptions = push(FadeTransition(duration))

fun FragmentNavOptions.fadePop(
    duration: Long = -1L
): FragmentNavOptions = pop(FadeTransition(duration))

fun FragmentNavOptions.horizontal(
    duration: Long = -1L
): FragmentNavOptions = horizontalPush(duration)
    .horizontalPop(duration)

fun FragmentNavOptions.horizontalPush(
    duration: Long = -1L
): FragmentNavOptions = push(HorizontalTransition(duration))

fun FragmentNavOptions.horizontalPop(
    duration: Long = -1L
): FragmentNavOptions = pop(HorizontalTransition(duration))

fun FragmentNavOptions.vertical(
    duration: Long = -1L
): FragmentNavOptions = verticalPush(duration)
    .verticalPop(duration)

fun FragmentNavOptions.verticalPush(
    duration: Long = -1L
): FragmentNavOptions = push(VerticalTransition(duration))

fun FragmentNavOptions.verticalPop(
    duration: Long = -1L
): FragmentNavOptions = pop(VerticalTransition(duration))

fun FragmentNavOptions.nonePop(): FragmentNavOptions = pop(null)
fun FragmentNavOptions.nonePush(): FragmentNavOptions = push(null)
fun FragmentNavOptions.none(): FragmentNavOptions =
    nonePush().nonePop()
