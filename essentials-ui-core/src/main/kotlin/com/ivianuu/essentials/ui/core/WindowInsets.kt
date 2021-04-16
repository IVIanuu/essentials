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

import android.view.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import androidx.core.view.WindowInsetsCompat
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import kotlin.math.*

@Composable
fun InsetsPadding(
    modifier: Modifier = Modifier,
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
    content: @Composable () -> Unit
) {
    val currentInsets = LocalInsets.current
    Box(
        modifier = Modifier.absolutePadding(
            if (left) currentInsets.left else 0.dp,
            if (top) currentInsets.top else 0.dp,
            if (right) currentInsets.right else 0.dp,
            if (bottom) currentInsets.bottom else 0.dp
        ).then(modifier)
    ) {
        ConsumeInsets(left, top, right, bottom, content)
    }
}

data class Insets(
    val left: Dp = 0.dp,
    val top: Dp = 0.dp,
    val right: Dp = 0.dp,
    val bottom: Dp = 0.dp
)

val LocalInsets = compositionLocalOf { Insets() }

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
        currentInsets.copy(
            left = if (left) 0.dp else currentInsets.left,
            top = if (top) 0.dp else currentInsets.top,
            right = if (right) 0.dp else currentInsets.right,
            bottom = if (bottom) 0.dp else currentInsets.bottom
        ),
        content = content
    )
}

@Composable
fun ProvideInsets(
    insets: Insets,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalInsets provides insets, content = content)
}

typealias WindowInsetsProvider = UiDecorator

@Given
fun windowInsetsProvider(): WindowInsetsProvider = { content ->
    val ownerView = LocalView.current
    val density = LocalDensity.current
    var insets by remember { mutableStateOf(Insets()) }

    val insetsListener = remember {
        View.OnApplyWindowInsetsListener { _, rawInsets ->
            val currentInsets =
                WindowInsetsCompat.toWindowInsetsCompat(rawInsets, ownerView)

            val systemBarInsets = currentInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = currentInsets.getInsets(WindowInsetsCompat.Type.ime())

            with(density) {
                insets = Insets(
                    left = max(systemBarInsets.left, imeInsets.left).toDp(),
                    top = max(systemBarInsets.top, imeInsets.top).toDp(),
                    right = max(systemBarInsets.right, imeInsets.right).toDp(),
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
fun localHorizontalInsetsPadding() = LocalInsets.current.let {
    PaddingValues(start = it.left, end = it.right)
}

@Composable
fun localVerticalInsetsPadding() = LocalInsets.current.let {
    PaddingValues(top = it.top, bottom = it.bottom)
}

fun Insets.toPaddingValues() = PaddingValues(
    start = left, top = top, end = right, bottom = bottom
)