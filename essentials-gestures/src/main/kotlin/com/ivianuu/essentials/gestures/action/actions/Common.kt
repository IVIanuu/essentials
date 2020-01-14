package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.graphics.Image
import coil.ImageLoader
import coil.api.getAny
import com.ivianuu.essentials.coroutines.flowOf
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.ui.image.toImage
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.DefinitionContext
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Factory
class CoilActionIconProvider(
    @Param private val data: Any,
    @Param private val tint: Boolean = false,
    private val imageLoader: ImageLoader
) : ActionIconProvider {
    override val icon: Flow<ActionIcon>
        get() = flowOf {
            ActionIcon(
                image = imageLoader.getAny(data).toImage(),
                tint = tint
            )
        }
}

fun SingleActionIconProvider(
    icon: Image,
    tint: Boolean = true
): ActionIconProvider = object : ActionIconProvider {
    override val icon: Flow<ActionIcon>
        get() = flowOf(ActionIcon(image = icon, tint = tint))
}

fun DefinitionContext.SingleActionIconProvider(id: Int): ActionIconProvider {
    val resourceProvider = get<ResourceProvider>()
    return SingleActionIconProvider(icon = resourceProvider.getDrawable(id))
}

fun DefinitionContext.getStringResource(id: Int) = get<ResourceProvider>().getString(id)

fun ActionExecutor.beforeAction(
    block: suspend () -> Unit
) = object : ActionExecutor {
    override suspend fun invoke() {
        block()
        this@beforeAction.invoke()
    }
}

fun ActionExecutor.afterAction(
    block: suspend () -> Unit
) = object : ActionExecutor {
    override suspend fun invoke() {
        this@afterAction.invoke()
        block()
    }
}
