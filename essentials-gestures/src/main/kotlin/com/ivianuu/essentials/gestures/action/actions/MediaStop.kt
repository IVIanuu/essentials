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

import android.view.KeyEvent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionSettingsKey
import com.ivianuu.injekt.FunBinding

@ActionBinding("media_stop")
fun stopMediaAction(mediaAction: mediaAction): Action = mediaAction(
    "media_stop",
    R.string.es_action_media_stop,
    singleActionIcon(R.drawable.es_ic_stop)
)

@ActionExecutorBinding("media_stop")
@FunBinding
suspend fun sendStopCommand(doMediaAction: doMediaAction) {
    doMediaAction(KeyEvent.KEYCODE_MEDIA_STOP)
}

@ActionSettingsKey("media_stop")
inline val MediaActionSettingsKey.stopeMediaActionSettingsKey: MediaActionSettingsKey
    get() = this
