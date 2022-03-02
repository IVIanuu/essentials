/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.media.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

@Provide object VolumeActionId : ActionId("volume")

@Provide fun volumeAction(RP: ResourceProvider) = Action(
  id = VolumeActionId,
  title = loadResource(R.string.es_action_volume),
  icon = staticActionIcon(R.drawable.es_ic_volume_up)
)

@Provide fun volumeActionExecutor(
  audioManager: @SystemService AudioManager
) = ActionExecutor<VolumeActionId> {
  audioManager.adjustStreamVolume(
    AudioManager.STREAM_MUSIC,
    AudioManager.ADJUST_SAME,
    AudioManager.FLAG_SHOW_UI
  )
}
