package com.ivianuu.essentials.systemoverlay.runner

import android.view.View
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.combine
import com.ivianuu.essentials.screenstate.DisplayInfo
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

typealias TriggerAttachManager = suspend () -> Unit

@Given
fun triggerAttachManager(
    @Given gesturesView: View,
    @Given position: TriggerPosition,
    @Given displayInfo: Flow<DisplayInfo>,
    @Given logger: Logger,
    @Given mainDispatcher: MainDispatcher,
    @Given prefs: Flow<TriggerPrefs>,
    @Given triggerGeometryFactory: TriggerGeometryFactory,
    @Given triggerRepository: TriggerRepository,
    @Given windowManager: AccessibilityWindowManager
): TriggerAttachManager = {
    logger.d { "start $position" }
    combine(triggerRepository.trigger(position), prefs.map { it.rotateTriggers })
        .distinctUntilChanged()
        .flatMapLatest { (trigger, rotateTriggers) ->
            logger.d { "trigger changed $trigger $rotateTriggers" }
            if (trigger.enabled) {
                displayInfo
                    .map { info ->
                        Attach(
                            triggerGeometryFactory(
                                trigger,
                                info.screenWidth,
                                info.screenHeight,
                                info.rotation,
                                rotateTriggers
                            )
                        )
                    }
            } else {
                flowOf(Detach)
            }
        }
        .onCancel { emit(Detach) }
        .distinctUntilChanged()
        .applyState<TriggerAttachMessage, TriggerAttachState>(Detached) { message ->
            when (message) {
                is Attach -> {
                    val view = (this as? Attached)?.view
                        ?: TriggerView(gesturesView)
                    val layoutParams = message.geometry.toLayoutParams()
                    logger.d { "attach $position" }
                    withContext(mainDispatcher) {
                        runCatching {
                            if (view.isAttachedToWindow) {
                                windowManager.updateViewLayout(view, layoutParams)
                            } else {
                                windowManager.addView(view, layoutParams)
                            }
                        }.onFailure {
                            logger.d { "failed to attach $position" }
                            runCatching { windowManager.removeViewImmediate(view) }
                        }
                    }.fold(
                        success = { Attached(view) },
                        failure = { Detached }
                    )
                }
                Detach -> when (this) {
                    Detached -> this
                    is Attached -> {
                        logger.d { "detach $position" }
                        withContext(mainDispatcher) {
                            runCatching {
                                windowManager.removeViewImmediate(view)
                            }
                        }
                        Detached
                    }
                }
            }
        }
        .collect()
}

internal sealed class TriggerAttachState {
    data class Attached(val view: View) : TriggerAttachState()
    object Detached : TriggerAttachState()
}

internal sealed class TriggerAttachMessage {
    data class Attach(val geometry: TriggerGeometry) : TriggerAttachMessage()
    object Detach : TriggerAttachMessage()
}
