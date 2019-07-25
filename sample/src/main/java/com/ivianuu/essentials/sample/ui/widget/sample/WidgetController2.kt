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

import android.graphics.Color
import android.view.Gravity.CENTER
import android.widget.LinearLayout.VERTICAL
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.builder.FrameLayout
import com.ivianuu.essentials.sample.ui.widget.builder.ImageView
import com.ivianuu.essentials.sample.ui.widget.builder.LinearLayout
import com.ivianuu.essentials.sample.ui.widget.builder.ProgressBar
import com.ivianuu.essentials.sample.ui.widget.builder.background
import com.ivianuu.essentials.sample.ui.widget.builder.image
import com.ivianuu.essentials.sample.ui.widget.builder.layoutGravity
import com.ivianuu.essentials.sample.ui.widget.builder.layoutSize
import com.ivianuu.essentials.sample.ui.widget.builder.matchParent
import com.ivianuu.essentials.sample.ui.widget.builder.onClick
import com.ivianuu.essentials.sample.ui.widget.builder.orientation
import com.ivianuu.essentials.sample.ui.widget.builder.padding
import com.ivianuu.essentials.sample.ui.widget.es.WidgetController
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.onActive
import com.ivianuu.essentials.sample.ui.widget.lib.state
import com.ivianuu.essentials.sample.ui.widget.material.Switch
import com.ivianuu.essentials.sample.ui.widget.view.TextView
import com.ivianuu.essentials.util.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun launchOnActive(block: suspend () -> Unit) = onActive {
    val job = GlobalScope.launch { block() }

    onDispose {
        job.cancel()
    }
}

class WidgetController2 : WidgetController() {

    override fun BuildContext.build() {
        +LinearLayout {
            orientation(VERTICAL)
            children {
                +SimpleTextToolbar(title = "Compose")
                +StatelessWidget {
                    val (loading, setLoading) = +state { true }

                    +launchOnActive {
                        delay(1000)
                        setLoading(false)
                    }

                    if (loading) {
                        +Loading()
                    } else {
                        +Content()
                    }
                }
            }
        }
    }

    private fun BuildContext.Loading() = StatelessWidget {
        +FrameLayout {
            matchParent()
            background(color = Color.BLUE)
            children {
                +ProgressBar {
                    layoutGravity(CENTER)
                }
            }
        }
    }

    private fun BuildContext.Content() = StatelessWidget {
        +LinearLayout {
            matchParent()
            children { +ListItem(0) }
        }
    }

    private fun BuildContext.ListItem(i: Int) = StatelessWidget {
        val (checked, setChecked) = +state { false }

        +ListItem(
            title = { +TextView(text = "Title $i") },
            subtitle = { +TextView(text = "Text $i") },
            leading = {
                +Switch(value = checked, onChange = {})
            },
            trailing = {
                +MenuButton()
            },
            onClick = { setChecked(!checked) }
        )
    }

    private fun BuildContext.MenuButton() = StatelessWidget {
        +ImageView {
            layoutSize(dp(40).toInt())
            padding(dp(8).toInt())
            background(attr = R.attr.selectableItemBackgroundBorderless)
            image(res = R.drawable.abc_ic_menu_overflow_material)
            onClick { d { "clicked" } }
        }
    }
}