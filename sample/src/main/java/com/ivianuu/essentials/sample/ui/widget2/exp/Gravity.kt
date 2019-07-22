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

package com.ivianuu.essentials.sample.ui.widget2.exp

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget

class Gravity(
    val gravity: Int,
    child: Widget,
    key: Any? = null
) : ViewPropsWidget(child, key) {

    override fun applyViewProps(context: BuildContext, view: View) {
        val lp = view.layoutParams
        when (lp) {
            is FrameLayout.LayoutParams -> lp.gravity = gravity
            is LinearLayout.LayoutParams -> lp.gravity = gravity
        }

        view.layoutParams = lp
    }

}