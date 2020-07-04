package com.ivianuu.essentials.gestures.action.actions

/**

@Module
fun volumeModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executor: VolumeActionExecutor ->
        Action(
key = "volume",
title = getString(R.string.es_action_volume),
iconProvider = SingleActionIconProvider(Icons.Default.VolumeUp),
            executor = executor
        ) as @StringKey("volume") Action
    }
}

@Unscoped
internal class VolumeActionExecutor(
    private val audioManager: AudioManager
) : ActionExecutor {
    override suspend fun invoke() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }
}
 */