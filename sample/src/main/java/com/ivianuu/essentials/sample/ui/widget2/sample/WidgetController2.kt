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
import com.ivianuu.essentials.sample.ui.widget2.hooks.HookWidget
import com.ivianuu.essentials.sample.ui.widget2.hooks.useState
import com.ivianuu.essentials.sample.ui.widget2.layout.FrameLayoutWidget
import com.ivianuu.essentials.sample.ui.widget2.layout.LinearLayoutWidget
import com.ivianuu.essentials.sample.ui.widget2.layout.ScrollViewWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget2.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget2.lib.Widget
import com.ivianuu.essentials.sample.ui.widget2.material.Checkbox
import com.ivianuu.essentials.sample.ui.widget2.material.MaterialButtonWidget
import com.ivianuu.essentials.sample.ui.widget2.util.WithParentElement
import com.ivianuu.essentials.sample.ui.widget2.view.Clickable
import com.ivianuu.essentials.sample.ui.widget2.view.DisableTouch
import com.ivianuu.essentials.sample.ui.widget2.view.Gravity
import com.ivianuu.essentials.sample.ui.widget2.view.ImageViewWidget
import com.ivianuu.essentials.sample.ui.widget2.view.Margin
import com.ivianuu.essentials.sample.ui.widget2.view.MatchParent
import com.ivianuu.essentials.sample.ui.widget2.view.Padding
import com.ivianuu.essentials.sample.ui.widget2.view.ProgressBarWidget
import com.ivianuu.essentials.sample.ui.widget2.view.Ripple
import com.ivianuu.essentials.sample.ui.widget2.view.Size
import com.ivianuu.essentials.sample.ui.widget2.view.WrapContent
import com.ivianuu.essentials.util.dp
import com.ivianuu.essentials.util.viewLifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WidgetController2 : WidgetController() {

    private val selections = mutableSetOf<Int>()

    private var loading = true

    override fun build(context: BuildContext): Widget {
        return LinearLayoutWidget(
            children = listOf(
                SimpleTextToolbar(title = "Compose"),
                WithParentElement { parent ->
                    if (loading) {
                        viewLifecycleScope.launch {
                            delay(2000)
                            loading = false
                            parent.markNeedsBuild()
                        }
                    }

                    if (loading) {
                        MatchParent(
                            child = FrameLayoutWidget(
                                children = listOf(
                                    WrapContent(
                                        child = Gravity(
                                            gravity = android.view.Gravity.CENTER,
                                            child = ProgressBarWidget()
                                        )
                                    )
                                )
                            )
                        )
                    } else {
                        MatchParent(
                            child = ScrollViewWidget(
                                child = MatchParent(
                                    LinearLayoutWidget(
                                        children = listOf(MyCounter()) + (1..10).map { ListItem(it) }
                                    )
                                )
                            )
                        )
                    }
                }
            )
        )
    }

    private fun ListItem(i: Int) = StatelessWidget {
        WithParentElement { parent ->
            ListItem(
                title = "Title $i",
                text = "Text $i",
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

                    parent.markNeedsBuild()
                }
            )
        }
    }

    private fun MenuButton() = StatelessWidget {
        Size(
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
}

class MyCounter(key: Any? = null) : HookWidget(key) {
    override fun build(context: BuildContext): Widget {
        val (count, setCount) = useState { 0 }
        val (count2, setCount2) = useState { 0 }
        return MaterialButtonWidget(
            text = "Count $count count 2 $count2",
            onClick = {
                setCount(count + 1)
                setCount2(count2 - 1)
            }
        )
    }
}