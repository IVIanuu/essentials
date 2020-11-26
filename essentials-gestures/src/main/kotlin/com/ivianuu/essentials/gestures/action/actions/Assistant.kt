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

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunBinding

@ActionBinding("assistant")
fun assistantAction(
    stringResource: stringResource,
    searchManager: SearchManager,
): Action = Action(
    key = "assistant",
    title = stringResource(R.string.es_action_assistant),
    unlockScreen = true,
    icon = singleActionIcon(R.drawable.es_ic_google)
)

@SuppressLint("DiscouragedPrivateApi")
@ActionExecutorBinding("assistant")
@FunBinding
suspend fun launchAssistant(searchManager: SearchManager) {
    val launchAssist = searchManager.javaClass
        .getDeclaredMethod("launchAssist", Bundle::class.java)
    launchAssist.invoke(searchManager, Bundle())
}
