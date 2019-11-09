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

package com.ivianuu.essentials.ui.compose.core

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.ui.core.Density
import androidx.ui.core.Size
import androidx.ui.core.dp
import androidx.ui.core.ipx
import androidx.ui.core.withDensity
import androidx.ui.layout.EdgeInsets
import com.ivianuu.essentials.util.containsFlag
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class AndroidComposeViewContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // todo use @Model once available

    val viewportMetrics: Flow<ViewportMetrics> get() = _viewportMetrics.asFlow()
    private val _viewportMetrics = ConflatedBroadcastChannel(ViewportMetrics())

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        withDensity(Density(context)) {
            _viewportMetrics.offer(
                _viewportMetrics.value.copy(
                    size = Size(w.ipx.toDp(), h.ipx.toDp())
                )
            )
        }
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val statusBarHidden = windowSystemUiVisibility.containsFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
        val navigationBarHidden =
            windowSystemUiVisibility.containsFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        var zeroSides = ZeroSides.None
        if (navigationBarHidden) {
            zeroSides = calculateShouldZeroSides()
        }

        withDensity(Density(context)) {
            val physicalPadding = EdgeInsets(
                left = if (zeroSides === ZeroSides.Left || zeroSides === ZeroSides.Both) 0.dp else insets.systemWindowInsetLeft.ipx.toDp(),
                top = if (statusBarHidden) 0.dp else insets.systemWindowInsetTop.ipx.toDp(),
                right = if (zeroSides === ZeroSides.Right || zeroSides === ZeroSides.Both) 0.dp else insets.systemWindowInsetRight.ipx.toDp(),
                bottom = 0.dp
            )

            val physicalViewInsets = EdgeInsets(
                left = 0.dp,
                top = 0.dp,
                right = 0.dp,
                bottom = if (navigationBarHidden) calculateBottomKeyboardInset(insets).ipx.toDp() else insets.systemWindowInsetBottom.ipx.toDp()
            )

            val systemGestureInsets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                EdgeInsets(
                    left = insets.systemGestureInsets.left.ipx.toDp(),
                    top = insets.systemGestureInsets.top.ipx.toDp(),
                    right = insets.systemGestureInsets.right.ipx.toDp(),
                    bottom = insets.systemGestureInsets.bottom.ipx.toDp()
                )
            } else {
                EdgeInsets()
            }

            val viewportMetrics = ViewportMetrics(
                size = _viewportMetrics.value.size,
                viewPadding = physicalPadding,
                viewInsets = physicalViewInsets,
                systemGestureInsets = systemGestureInsets
            )

            _viewportMetrics.offer(viewportMetrics)
        }

        return super.onApplyWindowInsets(insets)
    }

    private fun calculateBottomKeyboardInset(insets: WindowInsets): Int {
        val screenHeight = this.rootView.height
        return if (insets.systemWindowInsetBottom.toDouble() < screenHeight.toDouble() * 0.18) 0 else insets.systemWindowInsetBottom
    }

    private enum class ZeroSides {
        None, Left, Right, Both
    }

    private fun calculateShouldZeroSides(): ZeroSides {
        val activity = context as Activity
        val orientation = activity.resources.configuration.orientation
        val rotation = activity.windowManager.defaultDisplay.rotation

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (rotation == Surface.ROTATION_90) {
                return ZeroSides.Right
            } else if (rotation == Surface.ROTATION_270) {
                return if (Build.VERSION.SDK_INT >= 23) ZeroSides.Left else ZeroSides.Right
            } else if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                return ZeroSides.Both
            }
        }

        return ZeroSides.None
    }

    data class ViewportMetrics(
        val size: Size = Size(0.dp, 0.dp),
        val viewPadding: EdgeInsets = EdgeInsets(),
        val viewInsets: EdgeInsets = EdgeInsets(),
        val systemGestureInsets: EdgeInsets = EdgeInsets()
    )

}