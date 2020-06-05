package com.ivianuu.essentials.gestures.action.actions

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn

@Module
private fun CameraModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("camera") Action>(
        key = "camera",
        title = { getStringResource(R.string.es_action_camera) },
        iconProvider = { SingleActionIconProvider(Icons.Default.PhotoCamera) },
        unlockScreen = { true },
        executor = {
            get<@Provider (Intent) -> IntentActionExecutor>()(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
        }
    )*/
}
