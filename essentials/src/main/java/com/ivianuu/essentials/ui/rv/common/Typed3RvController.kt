package com.ivianuu.essentials.ui.rv.common

import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.ui.rv.RvController
import com.ivianuu.essentials.ui.rv.RvPlugins
import java.util.concurrent.Executor

/***
 * @author Manuel Wrage (IVIanuu)
 */
abstract class Typed3RvController<A, B, C>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor
) : RvController(diffingExecutor, buildingExecutor) {

    val data1 get() = _data1
    private var _data1: A? = null

    val data2 get() = _data2
    private var _data2: B? = null

    val data3 get() = _data3
    private var _data3: C? = null

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
        buildModels(data1!!, data2!!, data3!!)
    }

    protected abstract fun buildModels(data1: A, data2: B, data3: C)

    fun setData(data1: A, data2: B, data3: C) {
        _data1 = data1
        _data2 = data2
        _data3 = data3
        allowModelBuildRequests = true
        requestModelBuild()
        allowModelBuildRequests = false
    }
}

class SimpleTyped3RvController<A, B, C>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor,
    private val buildModels: (A, B, C) -> Unit
) : Typed3RvController<A, B, C>(diffingExecutor, buildingExecutor) {
    override fun buildModels(data1: A, data2: B, data3: C) {
        buildModels.invoke(data1, data2, data3)
    }
}

fun <A, B, C> typed3RvController(buildModels: (A, B, C) -> Unit) =
    SimpleTyped3RvController(buildModels = buildModels)

fun <A, B, C> RecyclerView.setTyped3RvController(buildModels: (A, B, C) -> Unit) =
    typed3RvController(buildModels).also { setRvController(it) }