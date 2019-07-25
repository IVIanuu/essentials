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

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.constraintlayout.widget.ConstraintSet
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.widget.builder.TextStyleAmbient
import com.ivianuu.essentials.sample.ui.widget.layout.ConstraintLayout
import com.ivianuu.essentials.sample.ui.widget.layout.ConstraintSetBuilder.Side.*
import com.ivianuu.essentials.sample.ui.widget.layout.PARENT_ID
import com.ivianuu.essentials.sample.ui.widget.layout.ViewConstraintBuilder
import com.ivianuu.essentials.sample.ui.widget.lib.BuildContext
import com.ivianuu.essentials.sample.ui.widget.lib.ContextAmbient
import com.ivianuu.essentials.sample.ui.widget.lib.StatelessWidget
import com.ivianuu.essentials.sample.ui.widget.lib.memo
import com.ivianuu.essentials.sample.ui.widget.view.Clickable
import com.ivianuu.essentials.sample.ui.widget.view.LongClickable
import com.ivianuu.essentials.sample.ui.widget.view.Ripple
import com.ivianuu.essentials.sample.ui.widget.view.Size
import com.ivianuu.kommon.core.content.dp

fun viewId() = memo { View.generateViewId() }

fun BuildContext.ListItem(
    title: (BuildContext.() -> Unit)? = null,
    subtitle: (BuildContext.() -> Unit)? = null,

    leading: (BuildContext.() -> Unit)? = null,
    trailing: (BuildContext.() -> Unit)? = null,

    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) = StatelessWidget("ListItem") {
    +Clickable(onClick = onClick ?: {}) {
        +LongClickable(onLongClick = onLongClick ?: {}) {
            +Ripple {
                val context = +ContextAmbient
                +Size(MATCH_PARENT, context.dp(72).toInt()) {
                    +ListItemContent(
                        title = title,
                        subtitle = subtitle,
                        leading = leading,
                        trailing = trailing
                    )
                }
            }
        }
    }
}

private fun BuildContext.ListItemContent(
    title: (BuildContext.() -> Unit)? = null,
    subtitle: (BuildContext.() -> Unit)? = null,

    leading: (BuildContext.() -> Unit)? = null,
    trailing: (BuildContext.() -> Unit)? = null
) = ConstraintLayout {
    if (leading != null) {
        leading()
    }

    if (title != null) {
        +TextStyleAmbient.Provider(R.style.TextAppearance_MaterialComponents_Subtitle1) {
            title()
        }
    }

    if (subtitle != null) {
        +TextStyleAmbient.Provider(R.style.TextAppearance_MaterialComponents_Body2) {
            subtitle()
        }
    }

    if (trailing != null) {
        trailing()
    }


    constraints {
        val leadingId = +viewId()
        val trailingId = +viewId()
        val titleId = +viewId()
        val subtitleId = +viewId()

        // layout leading
        if (leading != null) {
            leadingId {
                connect(
                    LEFT to LEFT of PARENT_ID,
                    TOP to TOP of PARENT_ID,
                    BOTTOM to BOTTOM of PARENT_ID
                )
            }
        }

        if (trailing != null) {
            trailingId {
                connect(
                    RIGHT to RIGHT of PARENT_ID,
                    TOP to TOP of PARENT_ID,
                    BOTTOM to BOTTOM of PARENT_ID
                )
            }
        }

        fun ViewConstraintBuilder.connectTextToSides() {
            if (leading != null) {
                connect(LEFT to RIGHT of leadingId)
            } else {
                connect(LEFT to LEFT of PARENT_ID)
            }
            if (trailing != null) {
                connect(RIGHT to LEFT of trailingId)
            } else {
                connect(RIGHT to RIGHT of PARENT_ID)
            }
        }

        if (title != null) {
            titleId {
                connectTextToSides()

                connect(TOP to TOP of PARENT_ID)
                if (subtitle != null) {
                    connect(BOTTOM to TOP of subtitleId)
                } else {
                    connect(BOTTOM to BOTTOM of PARENT_ID)
                }
            }
        }

        if (subtitle != null) {
            subtitleId {
                connectTextToSides()
                connect(BOTTOM to BOTTOM of PARENT_ID)
                if (title != null) {
                    connect(TOP to BOTTOM of titleId)
                } else {
                    connect(TOP to TOP of PARENT_ID)
                }
            }
        }

        if (title != null && subtitle != null) {
            createVerticalChain(
                PARENT_ID,
                ConstraintSet.TOP,
                PARENT_ID,
                ConstraintSet.BOTTOM,
                intArrayOf(titleId, subtitleId),
                null,
                ConstraintSet.CHAIN_PACKED
            )
        }
    }
}