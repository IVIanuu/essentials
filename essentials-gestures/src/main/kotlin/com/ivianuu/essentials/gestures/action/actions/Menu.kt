/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun

@Given object MenuActionId : ActionId("menu")

@ActionBinding<MenuActionId>
@Given
fun menuAction(
    @Given choosePermissions: choosePermissions,
    @Given stringResource: stringResource,
    @Given simulateMenuButtonPress: simulateMenuButtonPress,
): Action = Action(
    id = "menu",
    title = stringResource(R.string.es_action_menu),
    icon = singleActionIcon(Icons.Default.MoreVert),
    permissions = choosePermissions { listOf(root) }
)

@ActionExecutorBinding<MenuActionId>
@GivenFun
suspend fun simulateMenuButtonPress(@Given runRootCommand: runRootCommand) {
    runRootCommand("input keyevent 82")
}
