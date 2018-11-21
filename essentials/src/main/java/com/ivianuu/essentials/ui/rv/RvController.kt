package com.ivianuu.essentials.ui.rv

import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class RvController(
    private val diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    private val buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor
) {

    val adapter = RvAdapter(this, diffingExecutor)

    var isBuildingModels = false
        private set

    private val currentModels = mutableListOf<RvModel<*>>()

    private var hasBuiltModelsEver = false

    private val buildModelsAction: () -> Unit = {
        currentModels.clear()
        isBuildingModels = true
        buildModels()
        isBuildingModels = false
        adapter.setModels(currentModels)
        hasBuiltModelsEver = true
    }

    open fun requestModelBuild() {
        if (isBuildingModels) {
            throw IllegalStateException("cannot call requestModelBuild() inside buildModels()")
        }
        if (hasBuiltModelsEver) {
            buildingExecutor.execute(buildModelsAction)
        } else {
            buildModelsAction()
        }
    }

    protected abstract fun buildModels()

    open fun add(vararg models: RvModel<*>) {
        currentModels.addAll(models)
    }

    open fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    }

    open fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    }
}