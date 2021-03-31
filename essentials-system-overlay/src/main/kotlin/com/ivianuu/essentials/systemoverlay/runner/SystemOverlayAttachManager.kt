package com.ivianuu.essentials.systemoverlay.runner

import androidx.compose.runtime.Composable
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.MainDispatcher
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

typealias SystemOverlayAttachManager<T> = suspend () -> Unit

@Given
fun <T : SystemOverlayId> systemOverlayAttachManager(
    @Given attachState: Flow<SystemOverlayAttachState<T>>,
    @Given id: T,
    @Given logger: Logger,
    @Given mainDispatcher: MainDispatcher,
    @Given uiFactory: () -> SystemOverlayUi<T>,
    @Given viewFactory: (@Given @Composable () -> Unit) -> OverlayComposeView,
    @Given windowManager: SystemOverlayWindowManager
): SystemOverlayAttachManager<T> = {
    logger.d { "$id start" }
    attachState
        .onCancel {
            logger.d { "$id stop" }
            emit(SystemOverlayAttachState.Detached)
        }
        .distinctUntilChanged()
        .applyState<SystemOverlayAttachState<T>, InternalSystemOverlayAttachState>(
            InternalSystemOverlayAttachState.Detached
        ) { state ->
            when (state) {
                is SystemOverlayAttachState.Attached -> {
                    val view = (this as? InternalSystemOverlayAttachState.Attached)?.view
                        ?: viewFactory(uiFactory())
                    logger.d { "$id ${if (this is InternalSystemOverlayAttachState.Attached) "re-attach"
                    else "attach"} ${state.layoutParams}" }
                    withContext(mainDispatcher) {
                        runCatching {
                            if (view.isAttachedToWindow) {
                                windowManager.updateViewLayout(view, state.layoutParams)
                            } else {
                                windowManager.addView(view, state.layoutParams)
                            }
                        }.onFailure {
                            logger.d { "$id failed to attach" }
                            runCatching { windowManager.removeViewImmediate(view) }
                        }
                    }.fold(
                        success = { InternalSystemOverlayAttachState.Attached(view) },
                        failure = { InternalSystemOverlayAttachState.Detached }
                    )
                }
                SystemOverlayAttachState.Detached -> when (this) {
                    InternalSystemOverlayAttachState.Detached -> this
                    is InternalSystemOverlayAttachState.Attached -> {
                        logger.d { "$id detach" }
                        withContext(mainDispatcher) {
                            runCatching {
                                windowManager.removeViewImmediate(view)
                                view.dispose()
                            }
                        }
                        InternalSystemOverlayAttachState.Detached
                    }
                }
            }
        }
        .collect()
}

private sealed class InternalSystemOverlayAttachState {
    data class Attached(val view: OverlayComposeView) : InternalSystemOverlayAttachState()
    object Detached : InternalSystemOverlayAttachState()
}
