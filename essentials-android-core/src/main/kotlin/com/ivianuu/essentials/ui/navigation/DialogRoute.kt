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

package com.ivianuu.essentials.ui.navigation

import androidx.compose.Composable
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.dialog.DialogWrapper

abstract class DialogRoute(
    opaque: Boolean = false,
    enterTransition: StackTransition? = null,
    exitTransition: StackTransition? = null,
    val dismissible: Boolean = true
) : Route(enterTransition, exitTransition, opaque) {

    @Composable
    override fun invoke() {
        DialogWrapper(
            dismissible = dismissible,
            dismissHandler = { onDismiss() }
        ) {
            dialog()
        }
    }

    @Composable
    protected open fun onDismiss() {
        NavigatorAmbient.current.pop(this)
    }

    @Composable
    protected abstract fun dialog()

}

fun DialogRoute(
    opaque: Boolean = true,
    transition: StackTransition? = FadeStackTransition(),
    dismissible: Boolean = false,
    dialog: @Composable () -> Unit
): DialogRoute = DialogRoute(opaque, transition, transition, dismissible, dialog)

fun DialogRoute(
    opaque: Boolean = true,
    enterTransition: StackTransition? = FadeStackTransition(),
    exitTransition: StackTransition? = FadeStackTransition(),
    dismissible: Boolean = false,
    dialog: @Composable () -> Unit
): DialogRoute = object : DialogRoute(opaque, enterTransition, exitTransition, dismissible) {
    @Composable
    override fun dialog() {
        dialog.invoke()
    }
}
