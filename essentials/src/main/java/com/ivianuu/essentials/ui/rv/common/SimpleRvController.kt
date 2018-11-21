package com.ivianuu.essentials.ui.rv.common

import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.ui.rv.RvController
import com.ivianuu.essentials.ui.rv.RvPlugins
import java.util.concurrent.Executor

/**
 * @author Manuel Wrage (IVIanuu)
 */
class SimpleRvController(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor,
    private val buildModels: () -> Unit
) : RvController(diffingExecutor, buildingExecutor) {
    override fun buildModels() {
        buildModels.invoke()
    }
}

fun simpleRvController(buildModels: () -> Unit) = SimpleRvController(buildModels = buildModels)

fun RecyclerView.setSimpleRvController(buildModels: () -> Unit) =
    simpleRvController(buildModels).also { setRvController(it) }

fun RecyclerView.setSimpleRvControllerAndBuild(buildModels: () -> Unit) =
    setSimpleRvController(buildModels).also { it.requestModelBuild() }