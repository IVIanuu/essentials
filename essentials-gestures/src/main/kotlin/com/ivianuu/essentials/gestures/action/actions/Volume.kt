/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

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
