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

package com.ivianuu.essentials.sample.ui.widget.sample

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.builder.TextView
import com.ivianuu.essentials.sample.ui.widget.builder.layoutGravity
import com.ivianuu.essentials.sample.ui.widget.builder.text
import com.ivianuu.essentials.sample.ui.widget.builder.textAppearance
import com.ivianuu.essentials.sample.ui.widget.builder.textColor
import com.ivianuu.essentials.sample.ui.widget.builder.wrapContent
import com.ivianuu.essentials.sample.ui.widget.layout.FrameLayout
import com.ivianuu.essentials.sample.ui.widget.lib.ContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.view.Background
import com.ivianuu.essentials.sample.ui.widget.view.Elevation
import com.ivianuu.essentials.sample.ui.widget.view.Size
import com.ivianuu.kommon.core.content.colorAttr
import com.ivianuu.kommon.core.content.dp

fun SimpleTextToolbar(title: String) = StatelessWidget("toolbar") {
    val context = +ContextAmbient

    +Elevation(context.dp(4)) {
        +Size(
            width = MATCH_PARENT,
            height = context.dp(56).toInt()
        ) {
            +Background(
                color = context.colorAttr(R.attr.colorPrimary)
            ) {
                +Elevation(elevation = context.dp(4)) {
                    +FrameLayout {
                        +TextView {
                            wrapContent()
                            layoutGravity(android.view.Gravity.CENTER)
                            text(title)
                            textAppearance(R.style.TextAppearance_MaterialComponents_Headline6)
                            textColor(context.colorAttr(R.attr.colorOnPrimary))
                        }
                    }
                }
            }
        }
    }
}