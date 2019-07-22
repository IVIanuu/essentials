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

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget2.es.WidgetController
import com.ivianuu.essentials.sample.ui.widget2.layout.LinearLayoutWidget
import com.ivianuu.essentials.sample.ui.widget2.layout.ScrollViewWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.sample.ui.widget2.material.Checkbox
import com.ivianuu.essentials.sample.ui.widget2.view.Clickable
import com.ivianuu.essentials.sample.ui.widget2.view.DisableTouch
import com.ivianuu.essentials.sample.ui.widget2.view.ImageViewWidget
import com.ivianuu.essentials.sample.ui.widget2.view.Margin
import com.ivianuu.essentials.sample.ui.widget2.view.MatchParent
import com.ivianuu.essentials.sample.ui.widget2.view.Padding
import com.ivianuu.essentials.sample.ui.widget2.view.Ripple
import com.ivianuu.essentials.sample.ui.widget2.view.Size
import com.ivianuu.essentials.util.dp

class WidgetController2 : WidgetController() {

    private val selections = mutableSetOf<Int>()

    override fun build(context: BuildContext): Widget {
        return LinearLayoutWidget(
            children = listOf(
                SimpleTextToolbar(title = "Compose"),
                MatchParent(
                    child = ScrollViewWidget(
                        child = MatchParent(
                            LinearLayoutWidget(
                                children = (1..10).map { i ->
                                    ListItem(
                                        title = "Title $i",
                                        text = "TextViewWidget $i",
                                        primaryAction = Margin(
                                            left = dp(16).toInt(),
                                            right = -dp(16).toInt(), // yep
                                            child = DisableTouch(
                                                child = Checkbox(
                                                    value = selections.contains(i),
                                                    onChange = {}
                                                )
                                            )
                                        ),
                                        secondaryAction = Margin(
                                            right = dp(8).toInt(),
                                            child = MenuButton()
                                        ),
                                        onClick = {
                                            if (selections.contains(i)) {
                                                selections.remove(i)
                                            } else {
                                                selections.add(i)
                                            }

                                            buildOwner?.rebuild()
                                        }
                                    )
                                }
                            )
                        )
                    )
                )
            )
        )
    }

    private fun MenuButton() = Size(
        size = dp(40).toInt(),
        child = Ripple(
            unbounded = true,
            child = Padding(
                padding = dp(8).toInt(),
                child = Clickable(
                    child = ImageViewWidget(
                        imageRes = R.drawable.abc_ic_menu_overflow_material
                    ),
                    onClick = { d { "clicked" } }
                )
            )
        )
    )
}
