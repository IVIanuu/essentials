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

package com.ivianuu.essentials.sample.ui.widget2.layout

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.ivianuu.essentials.sample.ui.widget2.exp.AndroidContext
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewWidget

class Spacer(
    val width: Int = 0,
    val height: Int = 0,
    key: Any? = null
) : ViewWidget<View>(key) {

    override fun updateView(context: BuildContext, view: View) {
        super.updateView(context, view)
        if (view.layoutParams.width != width || view.layoutParams.height != height) {
            view.updateLayoutParams {
                width = this@Spacer.width
                height = this@Spacer.height
            }
        }
    }

    override fun createView(context: BuildContext) =
        View(AndroidContext(context)).apply {
            layoutParams = ViewGroup.LayoutParams(this@Spacer.width, this@Spacer.height)
        }

}