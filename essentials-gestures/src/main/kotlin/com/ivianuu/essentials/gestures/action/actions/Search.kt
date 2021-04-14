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

import android.content.ComponentName
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.util.LoadStringResourceUseCase
import com.ivianuu.injekt.Given

@Given
object SearchActionId : ActionId("search")

@Given
fun searchAction(
    @Given stringResource: LoadStringResourceUseCase
): @ActionBinding<SearchActionId> Action = Action(
    id = "search",
    title = stringResource(R.string.es_action_search, emptyList()),
    icon = singleActionIcon(Icons.Default.Search)
)

@Given
fun searchActionExecutor(
    @Given intentSender: ActionIntentSender
): @ActionExecutorBinding<SearchActionId> ActionExecutor = {
    intentSender(
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.google.android.googlequicksearchbox",
                "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
