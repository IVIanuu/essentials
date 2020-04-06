package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PhotoCamera
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get
import com.ivianuu.injekt.parametersOf

@ApplicationScope
@Module
private fun ComponentBuilder.cameraAction() {
    action(
        key = "camera",
        title = { getStringResource(R.string.es_action_camera) },
        iconProvider = { SingleActionIconProvider(Icons.Default.PhotoCamera) },
        unlockScreen = { true },
        executor = {
            get<IntentActionExecutor>(parameters = parametersOf(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)))
        }
    )
}
