package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun CameraModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executorFactory: @Provider (Intent) -> IntentActionExecutor ->
        Action(
key = "camera",
title = getString(R.string.es_action_camera),
iconProvider = SingleActionIconProvider(Icons.Default.PhotoCamera),
            executor = executorFactory(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)),
            unlockScreen = true
        ) as @StringKey("camera") Action
    }
}
 */