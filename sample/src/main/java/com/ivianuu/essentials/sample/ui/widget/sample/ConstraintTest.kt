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
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.constraintlayout.widget.ConstraintSet
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.layout.ConstraintLayout
import com.ivianuu.essentials.sample.ui.widget.layout.PARENT_ID
import com.ivianuu.essentials.sample.ui.widget.lib.AndroidContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.memo
import com.ivianuu.essentials.sample.ui.widget.view.Background
import com.ivianuu.essentials.sample.ui.widget.view.Id
import com.ivianuu.essentials.sample.ui.widget.view.Size
import com.ivianuu.essentials.sample.ui.widget.view.TextView
import com.ivianuu.kommon.core.content.dp

fun viewId() = memo { View.generateViewId() }

fun ConstraintTest() = StatelessWidget("ConstraintTest") {
    val androidContext = +AndroidContextAmbient
    +Background(color = Color.BLUE) {
        +Size(
            width = MATCH_PARENT,
            height = androidContext.dp(300).toInt()
        ) {
            +ConstraintLayout {

                val titleId = +viewId()
                +Id(id = titleId) {
                    +TextView(
                        text = "Hello",
                        textColor = Color.WHITE,
                        textAppearance = R.style.TextAppearance_MaterialComponents_Headline4
                    )
                }

                val subtitleId = +viewId()
                +Id(id = subtitleId) {
                    +TextView(
                        text = "World",
                        textColor = Color.WHITE,
                        textAppearance = R.style.TextAppearance_MaterialComponents_Headline4
                    )
                }

                constraints {
                    constraintSet.constrainWidth(titleId, WRAP_CONTENT)
                    constraintSet.constrainHeight(titleId, WRAP_CONTENT)
                    constraintSet.centerHorizontally(titleId, PARENT_ID)

                    constraintSet.constrainWidth(subtitleId, WRAP_CONTENT)
                    constraintSet.constrainHeight(subtitleId, WRAP_CONTENT)
                    constraintSet.centerHorizontally(subtitleId, PARENT_ID)

                    constraintSet.createVerticalChain(
                        PARENT_ID,
                        ConstraintSet.TOP,
                        PARENT_ID,
                        ConstraintSet.BOTTOM,
                        intArrayOf(titleId, subtitleId),
                        null,
                        ConstraintSet.CHAIN_PACKED
                    )

                    constraintSet.connect(
                        titleId,
                        ConstraintSet.BOTTOM,
                        subtitleId,
                        ConstraintSet.TOP,
                        androidContext.dp(56).toInt()
                    )

                    constraintSet.connect(
                        subtitleId,
                        ConstraintSet.TOP,
                        titleId,
                        ConstraintSet.BOTTOM
                    )
                }
            }
        }
    }
}