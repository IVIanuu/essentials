package com.ivianuu.essentials.screenstate

import android.util.*
import android.view.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*

data class DisplayInfo(
    val rotation: DisplayRotation = DisplayRotation.PORTRAIT_UP,
    val screenWidth: Int = 0,
    val screenHeight: Int = 0
)

@Given
fun displayInfo(
    @Given configChanges: () -> Flow<ConfigChange>,
    @Given displayRotation: () -> Flow<DisplayRotation>,
    @Given scope: GivenCoroutineScope<AppGivenScope>,
    @Given windowManager: @SystemService WindowManager
): @Scoped<AppGivenScope> Flow<DisplayInfo> = flow {
    combine(configChanges().onStart { emit(Unit) }, displayRotation()) { _, rotation ->
        metricsMutex.withLock {
            windowManager.defaultDisplay.getRealMetrics(metrics)
            DisplayInfo(
                rotation = rotation,
                screenWidth = metrics.widthPixels,
                screenHeight = metrics.heightPixels
            )
        }
    }.let { emitAll(it) }
}
    .shareIn(scope, SharingStarted.WhileSubscribed(), 1)
    .distinctUntilChanged()

private val metrics = DisplayMetrics()
private val metricsMutex = Mutex()
