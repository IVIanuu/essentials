package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.coil.CoilImage
import com.ivianuu.essentials.coroutines.flowOf
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Transient
internal class CoilActionIconProvider(
    private val data: @Assisted Any
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = flowOf {
            { CoilImage(data = data) }
        }
}

internal fun SingleActionIconProvider(
    icon: @Composable () -> Unit
): ActionIconProvider = object : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = flowOf(icon)
}

internal fun SingleActionIconProvider(
    icon: VectorAsset
): ActionIconProvider = SingleActionIconProvider { Icon(icon) }

internal fun SingleActionIconProvider(
    id: Int
): ActionIconProvider = SingleActionIconProvider { Icon(vectorResource(id)) }

internal fun ActionExecutor.beforeAction(
    block: suspend () -> Unit
) = object : ActionExecutor {
    override suspend fun invoke() {
        block()
        this@beforeAction.invoke()
    }
}

internal fun ActionExecutor.afterAction(
    block: suspend () -> Unit
) = object : ActionExecutor {
    override suspend fun invoke() {
        this@afterAction.invoke()
        block()
    }
}
