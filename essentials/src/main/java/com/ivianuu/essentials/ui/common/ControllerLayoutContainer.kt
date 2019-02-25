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

package com.ivianuu.essentials.ui.common

import android.view.View
import com.ivianuu.director.Controller
import com.ivianuu.director.doOnPostDestroy
import com.ivianuu.director.doOnPostUnbindView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*

private val layoutContainersByController =
    mutableMapOf<Controller, LayoutContainer>()

val Controller.layoutContainer: LayoutContainer
    get() = layoutContainersByController.getOrPut(this) {
        ControllerLayoutContainer(this)
            .also { doOnPostDestroy { layoutContainersByController.remove(this) } }
    }

class ControllerLayoutContainer(private val controller: Controller) : LayoutContainer {

    override val containerView: View?
        get() = controller.view

    init {
        controller.doOnPostUnbindView { clearFindViewByIdCache() }
    }

}