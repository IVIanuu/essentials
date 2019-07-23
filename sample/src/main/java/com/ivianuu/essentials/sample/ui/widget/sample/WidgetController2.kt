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

import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.es.WidgetController
import com.ivianuu.essentials.sample.ui.widget.layout.FrameLayout
import com.ivianuu.essentials.sample.ui.widget.layout.LinearLayout
import com.ivianuu.essentials.sample.ui.widget.layout.ScrollView
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.Widget
import com.ivianuu.essentials.sample.ui.widget.lib.state
import com.ivianuu.essentials.sample.ui.widget.material.CheckBox
import com.ivianuu.essentials.sample.ui.widget.view.Clickable
import com.ivianuu.essentials.sample.ui.widget.view.DisableTouch
import com.ivianuu.essentials.sample.ui.widget.view.Gravity
import com.ivianuu.essentials.sample.ui.widget.view.ImageView
import com.ivianuu.essentials.sample.ui.widget.view.Margin
import com.ivianuu.essentials.sample.ui.widget.view.MatchParent
import com.ivianuu.essentials.sample.ui.widget.view.Padding
import com.ivianuu.essentials.sample.ui.widget.view.ProgressBar
import com.ivianuu.essentials.sample.ui.widget.view.Ripple
import com.ivianuu.essentials.sample.ui.widget.view.Size
import com.ivianuu.essentials.sample.ui.widget.view.WrapContent
import com.ivianuu.essentials.util.dp
import com.ivianuu.essentials.util.viewLifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WidgetController2 : WidgetController() {

    override fun BuildContext.build(): Widget {
        return LinearLayout(
            children = listOf(
                SimpleTextToolbar(title = "Compose"),
                StatelessWidget("content") {
                    val (loading, setLoading) = state { true }(it)

                    if (loading) {
                        viewLifecycleScope.launch {
                            delay(2000)
                            setLoading(false)
                        }

                        Loading()
                    } else {

                        Content()
                    }
                }
            )
        )
    }

    private fun BuildContext.Loading() = MatchParent(
        child = FrameLayout(
            children = listOf(
                WrapContent(
                    child = Gravity(
                        gravity = android.view.Gravity.CENTER,
                        child = ProgressBar()
                    )
                )
            )
        )
    )

    private fun BuildContext.Content() = MatchParent(
        child = ScrollView(
            child = MatchParent(
                LinearLayout(
                    children = (1..10).map { ListItem(it) }
                )
            )
        )
    )

    private fun BuildContext.ListItem(i: Int) = StatelessWidget("ListItem") {
        val (checked, setChecked) = state { false }(it)

        ListItem(
            title = "Title $i",
            text = "Text $i",
            primaryAction = Margin(
                left = dp(16).toInt(),
                right = -dp(16).toInt(), // yep
                child = DisableTouch(
                    child = CheckBox(
                        value = checked,
                        onChange = {}
                    )
                )
            ),
            secondaryAction = Margin(
                right = dp(8).toInt(),
                child = MenuButton()
            ),
            onClick = { setChecked(!checked) }
        )
    }

    private fun BuildContext.MenuButton() = StatelessWidget("MenuButton") {
        Size(
            size = dp(40).toInt(),
            child = Ripple(
                unbounded = true,
                child = Padding(
                    padding = dp(8).toInt(),
                    child = Clickable(
                        child = ImageView(
                            imageRes = R.drawable.abc_ic_menu_overflow_material
                        ),
                        onClick = { d { "clicked" } }
                    )
                )
            )
        )
    }
}