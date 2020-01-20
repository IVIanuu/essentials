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
import android.os.Build
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.compose.Composable
import androidx.compose.Stable
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.core.content.getSystemService
import androidx.ui.core.AndroidComposeViewAmbient
import androidx.ui.core.ambientDensity
import androidx.ui.layout.EdgeInsets
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import androidx.ui.unit.withDensity
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.composehelpers.ambientOf
import com.ivianuu.essentials.composehelpers.current
import com.ivianuu.essentials.ui.common.UpdateProvider
import com.ivianuu.essentials.ui.common.Updateable
import com.ivianuu.essentials.ui.common.framed
import com.ivianuu.essentials.util.containsFlag
import android.view.WindowInsets as AndroidWindowInsets

@Composable
fun WindowInsetsManager(children: @Composable() () -> Unit) {
    val composeView = AndroidComposeViewAmbient.current

    val density = ambientDensity()
    val (windowInsets, setWindowInsets) = state { WindowInsets() }

    val insetsListener = remember {
        View.OnApplyWindowInsetsListener { _, insets ->
            val statusBarHidden =
                composeView.windowSystemUiVisibility.containsFlag(View.SYSTEM_UI_FLAG_FULLSCREEN)
            val navigationBarHidden =
                composeView.windowSystemUiVisibility.containsFlag(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

            val zeroSides = if (navigationBarHidden) calculateZeroSides(composeView.context)
            else ZeroSides.None

            withDensity(density) {
                val viewPadding = EdgeInsets(
                    left = if (zeroSides === ZeroSides.Left || zeroSides === ZeroSides.Both) 0.dp else insets.systemWindowInsetLeft.ipx.toDp(),
                    top = if (statusBarHidden) 0.dp else insets.systemWindowInsetTop.ipx.toDp(),
                    right = if (zeroSides === ZeroSides.Right || zeroSides === ZeroSides.Both) 0.dp else insets.systemWindowInsetRight.ipx.toDp(),
                    bottom = if (navigationBarHidden) 0.dp else insets.systemWindowInsetBottom.ipx.toDp()
                )

                val viewInsets = EdgeInsets(
                    left = 0.dp,
                    top = 0.dp,
                    right = 0.dp,
                    bottom = if (navigationBarHidden) calculateBottomKeyboardInset(
                        composeView,
                        insets
                    ).ipx.toDp() else insets.systemWindowInsetBottom.ipx.toDp()
                )

                val newWindowInsets = WindowInsets(viewPadding, viewInsets)

                if (windowInsets != newWindowInsets) {
                    d { "windows insets changed $newWindowInsets" }
                    setWindowInsets(newWindowInsets)
                }
            }

            return@OnApplyWindowInsetsListener insets
        }
    }

    val attachListener = remember {
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                composeView.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View?) {
            }
        }
    }

    onCommit(composeView) {
        composeView.setOnApplyWindowInsetsListener(insetsListener)
        composeView.addOnAttachStateChangeListener(attachListener)

        if (composeView.isAttachedToWindow) {
            composeView.requestApplyInsets()
        }

        onDispose {
            composeView.setOnApplyWindowInsetsListener(null)
            composeView.removeOnAttachStateChangeListener(attachListener)
        }
    }

    WindowInsetsProvider(value = windowInsets, children = children)
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
            return if (Build.VERSION.SDK_INT >= 23) ZeroSides.Left else ZeroSides.Right
        } else if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            return ZeroSides.Both
        }
    }

    return ZeroSides.None
}

// todo add padding field
@Stable
interface WindowInsets : Updateable<WindowInsets> {
    val viewPadding: EdgeInsets
    val viewInsets: EdgeInsets

    fun copy(
        viewPadding: EdgeInsets = this.viewPadding,
        viewInsets: EdgeInsets = this.viewInsets
    ): WindowInsets
}

fun WindowInsets(
    viewPadding: EdgeInsets = EdgeInsets(),
    viewInsets: EdgeInsets = EdgeInsets()
): WindowInsets {
    return ObservableWindowInsets(
        viewPadding = viewPadding,
        viewInsets = viewInsets
    )
}

@Stable
private class ObservableWindowInsets(
    viewPadding: EdgeInsets,
    viewInsets: EdgeInsets
) : WindowInsets {

    constructor(
        other: WindowInsets
    ) : this(
        viewPadding = other.viewPadding,
        viewInsets = other.viewInsets
    )

    override var viewPadding by framed(viewPadding)
    override var viewInsets by framed(viewInsets)

    override fun copy(viewPadding: EdgeInsets, viewInsets: EdgeInsets): WindowInsets =
        ObservableWindowInsets(
            viewPadding = viewPadding, viewInsets = viewInsets
        )

    override fun updateFrom(other: WindowInsets) {
        viewPadding = other.viewPadding
        viewInsets = other.viewInsets
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObservableWindowInsets

        if (viewPadding != other.viewPadding) return false
        if (viewInsets != other.viewInsets) return false

        return true
    }

    override fun hashCode(): Int {
        var result = viewPadding.hashCode()
        result = 31 * result + viewInsets.hashCode()
        return result
    }

    override fun toString(): String {
        return "ObservableWindowsInsets(viewPadding='$viewPadding', viewInsets='$viewInsets')"
    }
}

@Composable
fun ambientWindowInsets(): WindowInsets = WindowInsetsAmbient.current

@Composable
fun WindowInsetsProvider(
    value: WindowInsets,
    children: @Composable() () -> Unit
) {
    WindowInsetsAmbient.UpdateProvider(
        value = value,
        children = children
    )
}

private val WindowInsetsAmbient =
    ambientOf<WindowInsets> {
        error("No window insets provided")
    }
