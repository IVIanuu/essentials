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

fun DialogRoute(
    enterTransition: StackTransition? = FadeStackTransition(),
    exitTransition: StackTransition? = FadeStackTransition(),
    opaque: Boolean = true,
    dismissible: Boolean = true,
    onDismiss: @Composable () -> Unit = {
        NavigatorAmbient.current.popTop()
    },
    dialog: @Composable () -> Unit
) = Route(enterTransition, exitTransition, opaque) {
    DialogWrapper(
        dismissible = dismissible,
        onDismiss = onDismiss,
        dialog = dialog
    )
}
