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
import com.ivianuu.essentials.util.stringResource

@ActionBinding("search")
fun searchAction(
    sendIntent: sendIntent,
    stringResource: stringResource,
): Action = Action(
    key = "search",
    title = stringResource(R.string.es_action_search),
    icon = singleActionIcon(Icons.Default.Search),
    execute = {
        sendIntent(
            Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(
                    "com.google.android.googlequicksearchbox",
                    "com.google.android.apps.gsa.queryentry.QueryEntryActivity"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
)
