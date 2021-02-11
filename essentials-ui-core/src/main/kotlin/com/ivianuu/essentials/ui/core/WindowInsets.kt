/*
 * Copyright 2020 Manuel Wrage
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

import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.injekt.GivenFun
import kotlin.math.max

@Composable
fun InsetsPadding(
    modifier: Modifier = Modifier,
    start: Boolean = true,
    top: Boolean = true,
    end: Boolean = true,
    bottom: Boolean = true,
    content: @Composable () -> Unit
) {
    val padding = LocalInsets.current
    Box(
        modifier = Modifier.absolutePadding(
            if (start) padding.calculateLeftPadding(LocalLayoutDirection.current) else 0.dp,
            if (top) padding.calculateTopPadding() else 0.dp,
            if (end) padding.calculateRightPadding(LocalLayoutDirection.current) else 0.dp,
            if (bottom) padding.calculateBottomPadding() else 0.dp
        ).then(modifier)
    ) {
        ConsumeInsets(start, top, end, bottom, content)
    }
}

val LocalInsets = compositionLocalOf { PaddingValues() }

@Composable
fun ConsumeInsets(
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    content: @Composable () -> Unit
) {
    val currentInsets = LocalInsets.current
    ProvideInsets(
        PaddingValues(
            if (left) 0.dp else currentInsets.calculateStartPadding(LocalLayoutDirection.current),
            if (top) 0.dp else currentInsets.calculateTopPadding(),
            if (right) 0.dp else currentInsets.calculateEndPadding(LocalLayoutDirection.current),
            if (bottom) 0.dp else currentInsets.calculateBottomPadding()
        ),
        content = content
    )
}

@Composable
fun ProvideInsets(
    insets: PaddingValues,
    content: @Composable () -> Unit,
) {
    Providers(LocalInsets provides insets, content = content)
}

@UiDecoratorBinding
@GivenFun
@Composable
fun ProvideWindowInsets(content: @Composable () -> Unit) {
    val ownerView = LocalView.current
    val density = LocalDensity.current
    var insets by rememberState { PaddingValues() }

    val insetsListener = remember {
        View.OnApplyWindowInsetsListener { _, rawInsets ->
            val currentInsets =
                WindowInsetsCompat.toWindowInsetsCompat(rawInsets, ownerView)

            val systemBarInsets = currentInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = currentInsets.getInsets(WindowInsetsCompat.Type.ime())

            with(density) {
                insets = PaddingValues(
                    start = max(systemBarInsets.left, imeInsets.left).toDp(),
                    top = max(systemBarInsets.top, imeInsets.top).toDp(),
                    end = max(systemBarInsets.right, imeInsets.right).toDp(),
                    bottom = max(systemBarInsets.bottom, imeInsets.bottom).toDp(),
                )
            }

            return@OnApplyWindowInsetsListener rawInsets
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

    DisposableEffect(ownerView) {
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

    ProvideInsets(insets, content)
}

@Composable
fun localHorizontalInsets() = LocalInsets.current.let {
    val layoutDirection = LocalLayoutDirection.current
    PaddingValues(
        start = it.calculateStartPadding(layoutDirection),
        end = it.calculateEndPadding(layoutDirection)
    )
}

@Composable
fun localVerticalInsets() = LocalInsets.current.let {
    PaddingValues(
        top = it.calculateTopPadding(),
        bottom = it.calculateBottomPadding()
    )
}
