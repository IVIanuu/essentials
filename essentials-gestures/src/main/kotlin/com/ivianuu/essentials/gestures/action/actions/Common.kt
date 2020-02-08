package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.graphics.vector.VectorAsset
import coil.ImageLoader
import com.ivianuu.essentials.coil.getAnyNoInline
import com.ivianuu.essentials.coroutines.flowOf
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.ui.image.toImage
import com.ivianuu.essentials.ui.painter.ImageRenderable
import com.ivianuu.essentials.ui.painter.Renderable
import com.ivianuu.essentials.ui.painter.VectorRenderable
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Factory
class CoilActionIconProvider(
    @Param private val data: Any,
    private val imageLoader: ImageLoader
) : ActionIconProvider {
    override val icon: Flow<Renderable>
        get() = flowOf { ImageRenderable(imageLoader.getAnyNoInline(data).toImage()) }
}

fun SingleActionIconProvider(
    icon: Renderable
): ActionIconProvider = object : ActionIconProvider {
    override val icon: Flow<Renderable>
        get() = flowOf(icon)
}

fun SingleActionIconProvider(
    icon: VectorAsset
): ActionIconProvider = SingleActionIconProvider(VectorRenderable(icon))

fun Component.getStringResource(id: Int) = get<ResourceProvider>().getString(id)

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
