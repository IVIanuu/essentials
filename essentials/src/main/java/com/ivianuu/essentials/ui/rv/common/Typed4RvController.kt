package com.ivianuu.essentials.ui.rv.common

import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.ui.rv.RvController
import com.ivianuu.essentials.ui.rv.RvPlugins
import java.util.concurrent.Executor

/***
 * @author Manuel Wrage (IVIanuu)
 */
abstract class Typed4RvController<A, B, C, D>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor
) : RvController(diffingExecutor, buildingExecutor) {

    val data1 get() = _data1
    private var _data1: A? = null

    val data2 get() = _data2
    private var _data2: B? = null

    val data3 get() = _data3
    private var _data3: C? = null

    val data4 get() = _data4
    private var _data4: D? = null

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
        buildModels(data1!!, data2!!, data3!!, data4!!)
    }

    protected abstract fun buildModels(data1: A, data2: B, data3: C, data4: D)

    fun setData(data1: A, data2: B, data3: C, data4: D) {
        _data1 = data1
        _data2 = data2
        _data3 = data3
        _data4 = data4
        allowModelBuildRequests = true
        requestModelBuild()
        allowModelBuildRequests = false
    }
}

class SimpleTyped4RvController<A, B, C, D>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor,
    private val buildModels: (A, B, C, D) -> Unit
) : Typed4RvController<A, B, C, D>(diffingExecutor, buildingExecutor) {
    override fun buildModels(data1: A, data2: B, data3: C, data4: D) {
        buildModels.invoke(data1, data2, data3, data4)
    }
}

fun <A, B, C, D> typed4RvController(buildModels: (A, B, C, D) -> Unit) =
    SimpleTyped4RvController(buildModels = buildModels)

fun <A, B, C, D> RecyclerView.setTyped3RvController(buildModels: (A, B, C, D) -> Unit) =
    typed4RvController(buildModels).also { setRvController(it) }