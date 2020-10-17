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

/*
 * Copyend 2019 Manuel Wrage
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.platform.ViewAmbient
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import com.ivianuu.essentials.ui.UiDecoratorBinding
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlin.math.max

@Composable
fun InsetsPadding(
    modifier: Modifier = Modifier,
    start: Boolean = true,
    top: Boolean = true,
    end: Boolean = true,
    bottom: Boolean = true,
    children: @Composable () -> Unit
) {
    val padding = InsetsAmbient.current
    Box(
        modifier = Modifier.absolutePadding(
            if (start) padding.start else 0.dp,
            if (top) padding.top else 0.dp,
            if (end) padding.end else 0.dp,
            if (bottom) padding.bottom else 0.dp
        ).then(modifier)
    ) {
        ConsumeInsets(start, top, end, bottom, children)
    }
}

internal val InsetsAmbient = ambientOf { PaddingValues() }

@Composable
fun currentInsets(): PaddingValues = InsetsAmbient.current

@Composable
fun ConsumeInsets(
    start: Boolean = true,
    top: Boolean = true,
    end: Boolean = true,
    bottom: Boolean = true,
    children: @Composable () -> Unit
) {
    val currentInsets = InsetsAmbient.current
    ProvideInsets(
        PaddingValues(
            if (start) 0.dp else currentInsets.start,
            if (top) 0.dp else currentInsets.top,
            if (end) 0.dp else currentInsets.end,
            if (bottom) 0.dp else currentInsets.bottom
        ),
        children = children
    )
}

@Composable
fun ProvideInsets(
    insets: PaddingValues,
    children: @Composable () -> Unit,
) {
    Providers(InsetsAmbient provides insets, children = children)
}

@UiDecoratorBinding
@FunBinding
@Composable
fun ProvideWindowInsets(children: @Assisted @Composable () -> Unit) {
    val ownerView = ViewAmbient.current
    val density = DensityAmbient.current
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

    ProvideInsets(insets, children)
}
