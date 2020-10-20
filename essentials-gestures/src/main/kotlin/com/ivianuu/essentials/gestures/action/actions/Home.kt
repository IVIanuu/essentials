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

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.accessibility.performGlobalAction
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext

private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"

@ActionBinding
fun homeAction(
    choosePermissions: choosePermissions,
    openHomeScreen: openHomeScreen,
    performGlobalAction: performGlobalAction,
    stringResource: stringResource,
): Action = Action(
    key = "home",
    title = stringResource(R.string.es_action_home),
    permissions = choosePermissions {
        if (needsHomeIntentWorkaround) emptyList()
        else listOf(accessibility)
    },
    icon = singleActionIcon(R.drawable.es_ic_action_home),
    execute = {
        if (needsHomeIntentWorkaround) openHomeScreen()
        else performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }
)

@FunBinding
fun openHomeScreen(
    applicationContext: ApplicationContext,
    sendIntent: sendIntent,
) {
    try {
        val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        applicationContext.sendBroadcast(intent)
    } catch (t: Throwable) {
        t.printStackTrace()
    }

    sendIntent(
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(
                Intent.CATEGORY_HOME
            )
        }
    )
}
