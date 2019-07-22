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
import androidx.core.view.updatePaddingRelative
import com.ivianuu.essentials.sample.ui.widget2.lib.ViewPropsWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget

open class Padding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    child: Widget,
    key: Any?
) : ViewPropsWidget(child, key) {

    constructor(
        padding: Int,
        child: Widget,
        key: Any? = null
    ) : this(padding, padding, padding, padding, child, key)

    override fun applyViewProps(view: View) {
        view.updatePaddingRelative(left, top, right, bottom)
    }

}