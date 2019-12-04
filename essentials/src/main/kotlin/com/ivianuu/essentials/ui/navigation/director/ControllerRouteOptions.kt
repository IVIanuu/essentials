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

package com.ivianuu.essentials.ui.navigation.director

import com.ivianuu.director.ControllerChangeHandler
import com.ivianuu.director.DefaultChangeHandler
import com.ivianuu.director.DirectorPlugins
import com.ivianuu.director.RouterTransaction
import com.ivianuu.director.common.changehandler.FadeChangeHandler
import com.ivianuu.director.common.changehandler.HorizontalChangeHandler
import com.ivianuu.director.common.changehandler.VerticalChangeHandler
import com.ivianuu.director.common.changehandler.defaultAnimationDuration
import com.ivianuu.director.defaultPopHandler
import com.ivianuu.director.defaultPushHandler
import com.ivianuu.director.defaultRemovesFromViewOnPush
import com.ivianuu.director.popChangeHandler
import com.ivianuu.director.pushChangeHandler

fun controllerRouteOptions(): ControllerRoute.Options = ControllerRoute.Options()

inline fun defaultControllerRouteOptionsOrElse(options: () -> ControllerRoute.Options): ControllerRoute.Options =
    defaultControllerRouteOptionsOrNull() ?: options()

fun defaultControllerRouteOptionsOrNull(): ControllerRoute.Options? {
    val pushHandler = DirectorPlugins.defaultPushHandler
    val popHandler = DirectorPlugins.defaultPopHandler
    return if (pushHandler != null || popHandler != null)
        ControllerRoute.Options().pushHandler(pushHandler).popHandler(popHandler)
    else null
}

fun ControllerRoute.Options.applyToTransaction(transaction: RouterTransaction): ControllerRoute.Options =
    apply {
        transaction.pushChangeHandler(pushHandler())
        transaction.popChangeHandler(popHandler())
    }

fun ControllerRoute.Options.handler(
    changeHandler: ControllerChangeHandler
): ControllerRoute.Options = pushHandler(changeHandler).popHandler(changeHandler)

fun ControllerRoute.Options.fade(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = fadePush(duration, removesFromViewOnPush)
    .fadePop(duration, removesFromViewOnPush)

fun ControllerRoute.Options.fadePush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = pushHandler(
    FadeChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerRoute.Options.fadePop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = popHandler(
    FadeChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerRoute.Options.horizontal(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = horizontalPush(duration, removesFromViewOnPush)
    .horizontalPop(duration, removesFromViewOnPush)

fun ControllerRoute.Options.horizontalPush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = pushHandler(
    HorizontalChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerRoute.Options.horizontalPop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = popHandler(
    HorizontalChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerRoute.Options.vertical(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = verticalPush(duration, removesFromViewOnPush)
    .verticalPop(duration, removesFromViewOnPush)

fun ControllerRoute.Options.verticalPush(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = pushHandler(
    VerticalChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerRoute.Options.verticalPop(
    duration: Long = DirectorPlugins.defaultAnimationDuration,
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = popHandler(
    VerticalChangeHandler(
        duration,
        removesFromViewOnPush
    )
)

fun ControllerRoute.Options.dialog(): ControllerRoute.Options =
    dialogPush().dialogPop()

fun ControllerRoute.Options.dialogPush(): ControllerRoute.Options =
    nonePush(false)

fun ControllerRoute.Options.dialogPop(): ControllerRoute.Options =
    nonePop(false)

fun ControllerRoute.Options.nonePop(
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = popHandler(
    DefaultChangeHandler(removesFromViewOnPush)
)

fun ControllerRoute.Options.nonePush(
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options = pushHandler(
    DefaultChangeHandler(removesFromViewOnPush)
)

fun ControllerRoute.Options.none(
    removesFromViewOnPush: Boolean = DirectorPlugins.defaultRemovesFromViewOnPush
): ControllerRoute.Options =
    nonePop(removesFromViewOnPush).nonePush(removesFromViewOnPush)

fun ControllerRoute.Options.defaultPop(): ControllerRoute.Options = popHandler(
    DirectorPlugins.defaultPopHandler
)

fun ControllerRoute.Options.defaultPush(): ControllerRoute.Options = pushHandler(
    DirectorPlugins.defaultPushHandler
)

fun ControllerRoute.Options.default(): ControllerRoute.Options =
    defaultPush().defaultPop()
