/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.sample.ui.widget.es

import android.view.View
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.util.cast

abstract class WidgetController : EsController() {

    override val layoutRes: Int
        get() = R.layout.controller_widget

    var buildContext: BuildContext? = null

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        buildContext = BuildContext(view.cast()) { buildWidgets() }
    }

    override fun onDestroyView(view: View) {
        buildContext = null
        super.onDestroyView(view)
    }

    abstract fun BuildContext.buildWidgets()
}