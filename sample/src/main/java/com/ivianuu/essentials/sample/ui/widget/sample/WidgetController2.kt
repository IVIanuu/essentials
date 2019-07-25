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
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.builder.FrameLayout
import com.ivianuu.essentials.sample.ui.widget.builder.ImageView
import com.ivianuu.essentials.sample.ui.widget.builder.LinearLayout
import com.ivianuu.essentials.sample.ui.widget.builder.ProgressBar
import com.ivianuu.essentials.sample.ui.widget.builder.Switch
import com.ivianuu.essentials.sample.ui.widget.builder.TextView
import com.ivianuu.essentials.sample.ui.widget.builder.background
import com.ivianuu.essentials.sample.ui.widget.builder.id
import com.ivianuu.essentials.sample.ui.widget.builder.image
import com.ivianuu.essentials.sample.ui.widget.builder.layoutConstraintBottomToBottom
import com.ivianuu.essentials.sample.ui.widget.builder.layoutConstraintLeftToLeftOf
import com.ivianuu.essentials.sample.ui.widget.builder.layoutConstraintRightToRightOf
import com.ivianuu.essentials.sample.ui.widget.builder.layoutConstraintTopToTopOf
import com.ivianuu.essentials.sample.ui.widget.builder.layoutGravity
import com.ivianuu.essentials.sample.ui.widget.builder.layoutSize
import com.ivianuu.essentials.sample.ui.widget.builder.matchParent
import com.ivianuu.essentials.sample.ui.widget.builder.onClick
import com.ivianuu.essentials.sample.ui.widget.builder.orientation
import com.ivianuu.essentials.sample.ui.widget.builder.padding
import com.ivianuu.essentials.sample.ui.widget.builder.text
import com.ivianuu.essentials.sample.ui.widget.builder.value
import com.ivianuu.essentials.sample.ui.widget.builder.wrapContent
import com.ivianuu.essentials.sample.ui.widget.es.WidgetController
import com.ivianuu.essentials.sample.ui.widget.layout.PARENT_ID
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.onActive
import com.ivianuu.essentials.sample.ui.widget.lib.state
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
    }

    private fun Loading() = FrameLayout {
        matchParent()
        background(color = Color.BLUE)
        children {
            +ProgressBar {
                layoutGravity(CENTER)
            }
        }
    }

    private fun Content() = LinearLayout {
        matchParent()
        children { +ListItem(0) }
    }

    private fun ListItem(i: Int) = StatelessWidget("ListItem") {
        val (checked, setChecked) = +state { false }

        val leadingId = +viewId()
        val trailingId = +viewId()
        val titleId = +viewId()
        val subtitleId = +viewId()

        +ListItem(
            title = {
                +TextView {
                    id(titleId)
                    wrapContent()

                    layoutConstraintLeftToLeftOf(PARENT_ID)
                    layoutConstraintRightToRightOf(PARENT_ID)
                    layoutConstraintTopToTopOf(PARENT_ID)
                    layoutConstraintBottomToBottom(PARENT_ID)

                    /*
                    updateLayoutParams {
                        it as ConstraintLayout.LayoutParams

                        it.leftToLeft = PARENT_ID
                        it.rightToRight = PARENT_ID
                        it.topToTop = PARENT_ID
                        it.bottomToTop = subtitleId
                        it.verticalChainStyle = ConstraintLayout.LayoutParams.CHAIN_PACKED

                        true
                    }*/
                    text("Title $i")
                }
            },
            subtitle = {
                +TextView {
                    id(subtitleId)
                    wrapContent()
                    text("Text $i")
                    updateLayoutParams {
                        it as ConstraintLayout.LayoutParams

                        it.leftToLeft = PARENT_ID
                        it.rightToRight = PARENT_ID
                        it.topToBottom = titleId
                        it.bottomToBottom = PARENT_ID
                        it.verticalChainStyle = ConstraintLayout.LayoutParams.CHAIN_PACKED

                        true
                    }
                }
            },
            leading = { +Switch { value(checked) } },
            trailing = { +MenuButton() }
        )
    }

    private fun MenuButton() = ImageView {
        layoutSize(dp(40).toInt())
        padding(dp(8).toInt())
        background(attr = R.attr.selectableItemBackgroundBorderless)
        image(res = R.drawable.abc_ic_menu_overflow_material)
        onClick { d { "clicked" } }
    }
}