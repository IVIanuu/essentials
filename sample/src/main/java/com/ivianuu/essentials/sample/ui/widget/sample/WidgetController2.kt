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

import android.content.Intent
import android.graphics.Color
import androidx.core.net.toUri
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.es.WidgetController
import com.ivianuu.essentials.sample.ui.widget.layout.FrameLayout
import com.ivianuu.essentials.sample.ui.widget.layout.LinearLayout
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.effectOf
import com.ivianuu.essentials.sample.ui.widget.lib.onActive
import com.ivianuu.essentials.sample.ui.widget.lib.state
import com.ivianuu.essentials.sample.ui.widget.material.Switch
import com.ivianuu.essentials.sample.ui.widget.view.Background
import com.ivianuu.essentials.sample.ui.widget.view.Clickable
import com.ivianuu.essentials.sample.ui.widget.view.Gravity
import com.ivianuu.essentials.sample.ui.widget.view.ImageView
import com.ivianuu.essentials.sample.ui.widget.view.MatchParent
import com.ivianuu.essentials.sample.ui.widget.view.Padding
import com.ivianuu.essentials.sample.ui.widget.view.ProgressBar
import com.ivianuu.essentials.sample.ui.widget.view.Ripple
import com.ivianuu.essentials.sample.ui.widget.view.Size
import com.ivianuu.essentials.sample.ui.widget.view.TextView
import com.ivianuu.essentials.sample.ui.widget.view.WrapContent
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
            +SimpleTextToolbar(title = "Compose")
            +StatelessWidget("content") {
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

    private fun Loading() = MatchParent {
        +FrameLayout {
            +WrapContent {
                +Gravity(gravity = android.view.Gravity.CENTER) {
                    +ProgressBar()
                }
            }
        }
    }

    private fun Content() = MatchParent {
        +ListItem(0)
    }

    private fun ListItem(i: Int) = StatelessWidget("ListItem") {
        val (checked, setChecked) = +state { false }

        +ListItem(
            title = {
                +Background(color = Color.BLUE) {
                    +WrapContent {
                        +TextView(text = "Title $i")
                    }
                }
            },
            ///subtitle = { +TextView(text = "Text $i") },
            leading = {
                +Switch(
                    value = checked,
                    onChange = {}
                )
            },
            trailing = {
                +MenuButton()
            },
            onClick = +launchUrlOnClick { "https://www.google.com" }
        )
    }

    fun launchUrlOnClick(url: () -> String) = effectOf<() -> Unit> {
        val context = ContextAmbient(context)
        return@effectOf {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = url().toUri()
            })
        }
    }

    private fun MenuButton() = StatelessWidget("MenuButton") {
        +Size(size = dp(40).toInt()) {
            +Ripple(unbounded = true) {
                +Padding(padding = dp(8).toInt()) {
                    +Clickable(
                        onClick = { d { "clicked" } },
                        child = {
                            +ImageView(
                                imageRes = R.drawable.abc_ic_menu_overflow_material
                            )
                        }
                    )
                }
            }
        }
    }
}