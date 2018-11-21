package com.ivianuu.essentials.ui.rv

import java.util.concurrent.Executor
import java.util.concurrent.Executors

private val defaultExecutor by lazy { Executors.newSingleThreadExecutor() }

/**
 * @author Manuel Wrage (IVIanuu)
 */
object RvPlugins {
    var defaultDiffingExecutor: Executor = defaultExecutor
    var defaultBuildingExecutor: Executor = defaultExecutor
}