package com.ivianuu.essentials.ui.rv.common

import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.ui.rv.RvController
import com.ivianuu.essentials.ui.rv.RvPlugins
import java.util.concurrent.Executor

/***
 * @author Manuel Wrage (IVIanuu)
 */
abstract class Typed2RvController<A, B>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor
) : RvController(diffingExecutor, buildingExecutor) {

    val data1 get() = _data1
    private var _data1: A? = null

    val data2 get() = _data2
    private var _data2: B? = null

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
        buildModels(data1!!, data2!!)
    }

    protected abstract fun buildModels(data1: A, data2: B)

    fun setData(data1: A, data2: B) {
        _data1 = data1
        _data2 = data2
        allowModelBuildRequests = true
        requestModelBuild()
        allowModelBuildRequests = false
    }
}

class SimpleTyped2RvController<A, B>(
    diffingExecutor: Executor = RvPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = RvPlugins.defaultBuildingExecutor,
    private val buildModels: (A, B) -> Unit
) : Typed2RvController<A, B>(diffingExecutor, buildingExecutor) {
    override fun buildModels(data1: A, data2: B) {
        buildModels.invoke(data1, data2)
    }
}

fun <A, B> typed2RvController(buildModels: (A, B) -> Unit) =
    SimpleTyped2RvController(buildModels = buildModels)

fun <A, B> RecyclerView.setTyped2RvController(buildModels: (A, B) -> Unit) =
    typed2RvController(buildModels).also { setRvController(it) }