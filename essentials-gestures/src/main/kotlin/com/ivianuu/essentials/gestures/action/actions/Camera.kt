package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get
import com.ivianuu.injekt.parametersOf

internal val EsCameraActionModule = Module {
    bindAction(
        key = "camera",
        title = { getStringResource(R.string.es_action_camera) },
        iconProvider = { SingleActionIconProvider(R.drawable.es_ic_photo_camera) },
        unlockScreen = { true },
        executor = {
            get<IntentActionExecutor> {
                parametersOf(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
            }
        }
    )
}
