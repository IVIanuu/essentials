package com.ivianuu.essentials.ui.rv.common

import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.ui.rv.RvController
import com.ivianuu.essentials.ui.rv.RvPlugins
import java.util.concurrent.Executor

/**
 * @author Manuel Wrage (IVIanuu)
 */
abstract class Typed1RvController<A>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor
) : RvController(diffingExecutor, buildingExecutor) {

    val data1 get() = _data1
    private var _data1: A? = null

    private var allowModelBuildRequests = false

    override fun requestModelBuild() {
        if (!allowModelBuildRequests) {
            throw IllegalStateException("cannot be called directly use setData() instead")
        }

        super.requestModelBuild()
    }

    final override fun buildModels() {
        if (!isBuildingModels) {
            throw IllegalStateException("cannot be called directly use setData() instead")
        }
        buildModels(data1!!)
    }

    protected abstract fun buildModels(data1: A)

    fun setData(data1: A) {
        _data1 = data1
        allowModelBuildRequests = true
        requestModelBuild()
        allowModelBuildRequests = false
    }
}

class SimpleTyped1RvController<A>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor,
    private val buildModels: (A) -> Unit
) : Typed1RvController<A>(diffingExecutor, buildingExecutor) {
    override fun buildModels(data1: A) {
        buildModels.invoke(data1)
    }
}

fun <A> typed1RvController(buildModels: (A) -> Unit) =
    SimpleTyped1RvController(buildModels = buildModels)

fun <A> RecyclerView.setTyped1RvController(buildModels: (A) -> Unit) =
    typed1RvController(buildModels).also { setRvController(it) }