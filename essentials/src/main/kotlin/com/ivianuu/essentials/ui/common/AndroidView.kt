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

package com.ivianuu.essentials.ui.common

import android.content.Context
import android.view.View
import android.view.View.MeasureSpec
import android.widget.FrameLayout
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.ui.core.AlignmentLine
import androidx.ui.core.AndroidComposeViewAmbient
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Draw
import androidx.ui.core.IntPx
import androidx.ui.core.Layout
import androidx.ui.core.MeasureScope
import androidx.ui.core.Placeable
import androidx.ui.core.PxPosition
import androidx.ui.core.hasBoundedHeight
import androidx.ui.core.hasBoundedWidth
import androidx.ui.core.hasTightHeight
import androidx.ui.core.hasTightWidth
import androidx.ui.core.ipx
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.ui.layout.NonNullSingleChildLayout

@Composable
fun AndroidView(view: View) {
    val context = ambient(ContextAmbient)
    val androidViewHolder = remember { AndroidViewHolder(context) }
    val composeView = ambient(AndroidComposeViewAmbient)
    onPreCommit(androidViewHolder, composeView) {
        composeView.addView(androidViewHolder)
        onDispose { composeView.removeView(androidViewHolder) }
    }
    remember(view) { androidViewHolder.view = view }

    AndroidViewWrapper {
        Layout(children = {}) { measureables, constraints ->
            val widthSpec = when {
                constraints.hasTightWidth -> {
                    MeasureSpec.makeMeasureSpec(constraints.maxWidth.value, MeasureSpec.EXACTLY)
                }
                constraints.hasBoundedWidth -> {
                    MeasureSpec.makeMeasureSpec(constraints.maxWidth.value, MeasureSpec.AT_MOST)
                }
                else -> {
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                }
            }
            val heightSpec = when {
                constraints.hasTightHeight -> {
                    MeasureSpec.makeMeasureSpec(constraints.maxHeight.value, MeasureSpec.EXACTLY)
                }
                constraints.hasBoundedHeight -> {
                    MeasureSpec.makeMeasureSpec(constraints.maxHeight.value, MeasureSpec.AT_MOST)
                }
                else -> {
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                }
            }
            if (constraints.minWidth != IntPx.Zero) {
                androidViewHolder.minimumWidth = constraints.minWidth.value
            }
            if (constraints.minHeight != IntPx.Zero) {
                androidViewHolder.minimumHeight = constraints.minHeight.value
            }
            androidViewHolder.measure(widthSpec, heightSpec)

            return@Layout object : MeasureScope.LayoutResult {
                override val width: IntPx = androidViewHolder.measuredWidth.ipx
                override val height: IntPx = androidViewHolder.measuredHeight.ipx
                override val alignmentLines: Map<AlignmentLine, IntPx> = mapOf()
                override fun placeChildren(placementScope: Placeable.PlacementScope) {
                    androidViewHolder.layout(
                        0,
                        0,
                        androidViewHolder.measuredWidth,
                        androidViewHolder.measuredHeight
                    )
                }
            }
        }

        Draw { canvas, size -> androidViewHolder.draw(canvas.nativeCanvas) }
    }
}

@Composable
private fun AndroidViewWrapper(
    children: @Composable() () -> Unit
) {
    NonNullSingleChildLayout(child = children) { measureable, constraints ->
        val placeable = measureable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.place(PxPosition.Origin)
        }
    }
}

private class AndroidViewHolder(context: Context) : FrameLayout(context) {
    var view: View? = null
        set(value) {
            if (value != field) {
                field = value
                removeAllViews()
                addView(view)
                d { "swap view $view" }
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        d { "on attach" }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        d { "on detach" }
    }
}
