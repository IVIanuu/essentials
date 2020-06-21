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

package com.ivianuu.essentials.ui.core

import android.content.Context
import android.content.res.Configuration
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.compose.Providers
import androidx.compose.StructurallyEqual
import androidx.compose.ambientOf
import androidx.compose.getValue
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.core.content.getSystemService
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.ViewAmbient
import androidx.ui.core.composed
import androidx.ui.layout.InnerPadding
import androidx.ui.layout.absolutePadding
import androidx.ui.unit.dp
import androidx.ui.unit.max
import com.ivianuu.essentials.util.containsFlag
import android.view.WindowInsets as AndroidWindowInsets

fun Modifier.systemBarsPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
): Modifier = composed {
    val padding = InsetsAmbient.current.systemBars
    absolutePadding(
        if (left) padding.start else 0.dp,
        if (top) padding.top else 0.dp,
        if (right) padding.end else 0.dp,
        if (bottom) padding.bottom else 0.dp
    )
}

fun Modifier.imePadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
): Modifier = composed {
    val padding = InsetsAmbient.current.ime
    absolutePadding(
        if (left) padding.start else 0.dp,
        if (top) padding.top else 0.dp,
        if (right) padding.end else 0.dp,
        if (bottom) padding.bottom else 0.dp
    )
}

fun Modifier.insetsPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
): Modifier = composed {
    val insets = InsetsAmbient.current
    absolutePadding(
        if (left) max(insets.systemBars.start, insets.ime.start) else 0.dp,
        if (top) max(insets.systemBars.top, insets.ime.top) else 0.dp,
        if (right) max(insets.systemBars.end, insets.ime.end) else 0.dp,
        if (bottom) max(insets.systemBars.bottom, insets.ime.bottom) else 0.dp
    )
}

@Immutable
data class Insets(
    val systemBars: InnerPadding,
    val ime: InnerPadding
) {
    companion object {
        val Empty = Insets(InnerPadding(), InnerPadding())
    }
}

val InsetsAmbient =
    ambientOf(StructurallyEqual) { Insets.Empty }

@Composable
fun ConsumeInsets(children: @Composable () -> Unit) {
    Providers(
        InsetsAmbient provides Insets.Empty,
        children = children
    )
}

@Composable
fun ProvideInsets(children: @Composable () -> Unit) {
    val ownerView = ViewAmbient.current
    val density = DensityAmbient.current
    var insets by state(StructurallyEqual) { Insets.Empty }

    val insetsListener = remember {
        View.OnApplyWindowInsetsListener { _, androidInsets ->
            val statusBarHidden =
                ownerView.windowSystemUiVisibility.containsFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            val navigationBarHidden =
                ownerView.windowSystemUiVisibility.containsFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

            val zeroSides = if (navigationBarHidden) calculateZeroSides(ownerView.context)
            else ZeroSides.None

            with(density) {
                val viewPadding = InnerPadding(
                    start = if (zeroSides === ZeroSides.Left || zeroSides === ZeroSides.Both) 0.dp else androidInsets.systemWindowInsetLeft.toDp(),
                    top = if (statusBarHidden) 0.dp else androidInsets.systemWindowInsetTop.toDp(),
                    end = if (zeroSides === ZeroSides.Right || zeroSides === ZeroSides.Both) 0.dp else androidInsets.systemWindowInsetRight.toDp(),
                    bottom = if (navigationBarHidden) 0.dp else androidInsets.systemWindowInsetBottom.toDp()
                )

                val viewInsets = InnerPadding(
                    start = 0.dp,
                    top = 0.dp,
                    end = 0.dp,
                    bottom = if (navigationBarHidden) calculateBottomKeyboardInset(
                        ownerView,
                        androidInsets
                    ).toDp() else androidInsets.systemWindowInsetBottom.toDp()
                )

                insets = Insets(viewPadding, viewInsets)
            }

            return@OnApplyWindowInsetsListener androidInsets
        }
    }

    val attachListener = remember {
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                ownerView.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View?) {
            }
        }
    }

    onCommit(ownerView) {
        ownerView.setOnApplyWindowInsetsListener(insetsListener)
        ownerView.addOnAttachStateChangeListener(attachListener)

        if (ownerView.isAttachedToWindow) {
            ownerView.requestApplyInsets()
        }

        onDispose {
            ownerView.setOnApplyWindowInsetsListener(null)
            ownerView.removeOnAttachStateChangeListener(attachListener)
        }
    }

    Providers(InsetsAmbient provides insets, children = children)
}

private fun calculateBottomKeyboardInset(
    view: View,
    insets: AndroidWindowInsets
): Int {
    val screenHeight = view.rootView.height
    return if (insets.systemWindowInsetBottom.toDouble() < screenHeight.toDouble() * 0.18) 0 else insets.systemWindowInsetBottom
}

private enum class ZeroSides {
    None, Left, Right, Both
}

private fun calculateZeroSides(
    context: Context
): ZeroSides {
    val orientation = context.resources.configuration.orientation
    val rotation = context.getSystemService<WindowManager>()!!.defaultDisplay.rotation

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        if (rotation == Surface.ROTATION_90) {
            return ZeroSides.Right
        } else if (rotation == Surface.ROTATION_270) {
            return ZeroSides.Left
        } else if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            return ZeroSides.Both
        }
    }

    return ZeroSides.None
}
