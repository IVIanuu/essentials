package com.ivianuu.essentials.screenstate

import android.util.DisplayMetrics
import android.view.WindowManager
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

data class DisplayInfo(
    val rotation: DisplayRotation = DisplayRotation.PORTRAIT_UP,
    val screenWidth: Int = 0,
    val screenHeight: Int = 0
)

@Given
fun displayInfo(
    @Given configChanges: () -> Flow<ConfigChange>,
    @Given displayRotation: () -> Flow<DisplayRotation>,
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
    @Given windowManager: WindowManager
): @Scoped<AppGivenScope> Flow<DisplayInfo> = flow {
    combine(configChanges().onStart { emit(Unit) }, displayRotation()) { _, rotation ->
        synchronized(metrics) {
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
