package com.ivianuu.essentials.gestures.action.actions

/**

@Module
fun TorchModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             iconProvider: TorchActionIconProvider,
             executor: TorchActionExecutor ->
        Action(
key = "torch",
title = getString(R.string.es_action_torch),
iconProvider = iconProvider,
            executor = executor
        ) as @StringKey("torch") Action
    }
}

@Unscoped
internal class TorchActionExecutor(
    private val torchManager: TorchManager
) : ActionExecutor {
    override suspend fun invoke() {
        torchManager.toggleTorch()
    }
}

@Unscoped
internal class TorchActionIconProvider(
    private val torchManager: TorchManager
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = torchManager.torchState
            .map {
                if (it) Icons.Default.FlashOn
                else Icons.Default.FlashOff
            }
            .map {
                {
                    Icon(it)
                }
            }
}
 */