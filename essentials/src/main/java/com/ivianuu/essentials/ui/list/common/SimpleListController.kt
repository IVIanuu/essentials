/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.list.common

import com.ivianuu.essentials.ui.list.ListController
import com.ivianuu.essentials.ui.list.ListPlugins
import com.ivianuu.essentials.ui.list.defaultBuildingExecutor
import com.ivianuu.essentials.ui.list.defaultDiffingExecutor
import java.util.concurrent.Executor

/**
 * Simple [ListController] which takes invokes [buildModels] to build models
 */
class SimpleListController(
    diffingExecutor: Executor = ListPlugins.defaultDiffingExecutor,
    buildingExecutor: Executor = ListPlugins.defaultBuildingExecutor,
    private val buildModels: ListController.() -> Unit
) : ListController(diffingExecutor, buildingExecutor) {
    override fun buildModels() {
        buildModels.invoke(this)
    }
}

fun listController(buildModels: ListController.() -> Unit) =
    SimpleListController(buildModels = buildModels)