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

package com.ivianuu.essentials.sample.ui.widget.behavior

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import com.ivianuu.essentials.sample.ui.widget.lib.HeadlessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget

open class Margin(
    child: Widget<*>,
    val left: Int = 0,
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0
) : HeadlessWidget(child) {

    constructor(
        child: Widget<*>,
        margin: Int
    ) : this(child, margin, margin, margin, margin)

    override fun bind(view: View) {
        super.bind(view)
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(left, top, right, bottom)
        }
    }

    override fun buildChildren() {
        emit(child)
    }

}