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

package com.ivianuu.essentials.sample.ui.widget2.sample

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget2.layout.FrameLayoutWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.sample.ui.widget2.view.Background
import com.ivianuu.essentials.sample.ui.widget2.view.Elevation
import com.ivianuu.essentials.sample.ui.widget2.view.Gravity
import com.ivianuu.essentials.sample.ui.widget2.view.Size
import com.ivianuu.essentials.sample.ui.widget2.view.TextViewWidget
import com.ivianuu.essentials.sample.ui.widget2.view.WrapContent
import com.ivianuu.kommon.core.content.colorAttr
import com.ivianuu.kommon.core.content.dp

class SimpleTextToolbar(val title: String) : StatelessWidget() {

    override fun build(context: BuildContext): Widget {
        return Size(
            width = MATCH_PARENT,
            height = AndroidContextAmbient(context).dp(56).toInt(),
            child = Background(
                color = AndroidContextAmbient(context).colorAttr(R.attr.colorPrimary),
                child = Elevation(
                    elevation = AndroidContextAmbient(context).dp(4),
                    child = FrameLayoutWidget(
                        children = listOf(
                            Gravity(
                                gravity = android.view.Gravity.CENTER,
                                child = WrapContent(
                                    child = TextViewWidget(
                                        text = title,
                                        textAppearance = R.style.TextAppearance_MaterialComponents_Headline6,
                                        textColor = AndroidContextAmbient(context).colorAttr(R.attr.colorOnPrimary)
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

}