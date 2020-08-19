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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.state
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.core.content.getSystemService
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.runtime.onPreCommit
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.ivianuu.essentials.util.containsFlag
import android.view.WindowInsets as AndroidWindowInsets

@Composable
fun SystemBarsPadding(
    modifier: Modifier = Modifier,
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    children: @Composable () -> Unit
) {
    val padding = InsetsAmbient.current.systemBars
    Box(
        modifier = Modifier.absolutePadding(
            if (left) padding.start else 0.dp,
            if (top) padding.top else 0.dp,
            if (right) padding.end else 0.dp,
            if (bottom) padding.bottom else 0.dp
        ) + modifier
    ) {
        ConsumeInsets(left, top, right, bottom, children)
    }
}

@Composable
fun ImePadding(
    modifier: Modifier = Modifier,
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    children: @Composable () -> Unit
) {
    val padding = InsetsAmbient.current.ime
    Box(
        modifier = Modifier.absolutePadding(
            if (left) padding.start else 0.dp,
            if (top) padding.top else 0.dp,
            if (right) padding.end else 0.dp,
            if (bottom) padding.bottom else 0.dp
        ) + modifier
    ) {
        ConsumeInsets(left, top, right, bottom, children)
    }
}

@Composable
fun InsetsPadding(
    modifier: Modifier = Modifier,
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    children: @Composable () -> Unit
) {
    val insets = InsetsAmbient.current
    Box(
        modifier = Modifier.absolutePadding(
            if (left) max(insets.systemBars.start, insets.ime.start) else 0.dp,
            if (top) max(insets.systemBars.top, insets.ime.top) else 0.dp,
            if (right) max(insets.systemBars.end, insets.ime.end) else 0.dp,
            if (bottom) max(insets.systemBars.bottom, insets.ime.bottom) else 0.dp
        ) + modifier
    ) {
        ConsumeInsets(left, top, right, bottom, children)
    }
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
    ambientOf(structuralEqualityPolicy()) { Insets.Empty }

@Composable
fun ConsumeInsets(
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    children: @Composable () -> Unit
) {
    val currentInsets = InsetsAmbient.current
    Providers(
        InsetsAmbient provides Insets(
            systemBars = currentInsets.systemBars.copy(
                if (left) 0.dp else currentInsets.systemBars.start,
                if (top) 0.dp else currentInsets.systemBars.top,
                if (right) 0.dp else currentInsets.systemBars.end,
                if (bottom) 0.dp else currentInsets.systemBars.bottom
            ),
            ime = currentInsets.ime.copy(
                if (left) 0.dp else currentInsets.ime.start,
                if (top) 0.dp else currentInsets.ime.top,
                if (right) 0.dp else currentInsets.ime.end,
                if (bottom) 0.dp else currentInsets.ime.bottom
            )
        ),
        children = children
    )
}

@Composable
fun ProvideInsets(children: @Composable () -> Unit) {
    val ownerView = ViewAmbient.current
    val density = DensityAmbient.current
    var insets by state(structuralEqualityPolicy()) { Insets.Empty }

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

    onPreCommit(ownerView) {
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
