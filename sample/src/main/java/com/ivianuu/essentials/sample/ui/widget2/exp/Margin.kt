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
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget

open class Margin(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    child: Widget,
    key: Any?
) : ViewPropsWidget(child, key) {

    constructor(
        margin: Int,
        child: Widget,
        key: Any? = null
    ) : this(margin, margin, margin, margin, child, key)

    override fun applyViewProps(context: BuildContext, view: View) {
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = left
            topMargin = top
            rightMargin = right
            bottomMargin = bottom
        }
    }

}