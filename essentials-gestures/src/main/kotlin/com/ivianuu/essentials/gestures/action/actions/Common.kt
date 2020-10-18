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

import android.app.PendingIntent
import android.content.Intent
import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.shell.runShellCommand
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.flow.flowOf

internal fun coilActionIcon(data: Any): ActionIcon = flowOf { CoilImage(data = data) }

internal fun singleActionIcon(icon: @Composable () -> Unit): ActionIcon = flowOf(icon)

internal fun singleActionIcon(icon: VectorAsset) = singleActionIcon { Icon(icon) }

internal fun singleActionIcon(id: Int) = singleActionIcon { Icon(vectorResource(id)) }

@FunBinding
suspend fun runRootCommand(
    runShellCommand: runShellCommand,
    showToastRes: showToastRes,
    command: @Assisted String,
) {
    try {
        runShellCommand(command)
    } catch (t: Throwable) {
        t.printStackTrace()
        showToastRes(R.string.es_no_root)
    }
}

@FunBinding
fun sendIntent(
    applicationContext: ApplicationContext,
    showToastRes: showToastRes,
    intent: @Assisted Intent,
) {
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        PendingIntent.getActivity(
            applicationContext, 99, intent, 0, null
        ).send()
    } catch (t: Throwable) {
        t.printStackTrace()
        showToastRes(R.string.es_activity_not_found)
    }
}
