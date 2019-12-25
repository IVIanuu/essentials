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

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.Modifier
import androidx.ui.core.PxPosition
import androidx.ui.core.TextSpanScope
import androidx.ui.core.currentTextStyle
import androidx.ui.core.selection.Selectable
import androidx.ui.core.selection.Selection
import androidx.ui.core.selection.SelectionRegistrar
import androidx.ui.core.selection.SelectionRegistrarAmbient
import androidx.ui.foundation.contentColor
import androidx.ui.text.AnnotatedString
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow

// todo remove once supported by original

@Composable
fun Text(
    modifier: Modifier = Modifier.None,
    style: TextStyle? = null,
    softWrap: Boolean = DefaultSoftWrap,
    overflow: TextOverflow = DefaultOverflow,
    maxLines: Int = DefaultMaxLines,
    selectable: Boolean = false,
    child: @Composable TextSpanScope.() -> Unit
) {
    ToggleableSelectable(
        selectable = selectable
    ) {
        Text(
            child = child,
            modifier = modifier,
            style = applyContentColorIfNeeded(style),
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines
        )
    }
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier.None,
    style: TextStyle? = null,
    softWrap: Boolean = DefaultSoftWrap,
    overflow: TextOverflow = DefaultOverflow,
    maxLines: Int = DefaultMaxLines,
    selectable: Boolean = false
) {
    ToggleableSelectable(
        selectable = selectable
    ) {
        Text(
            text = text,
            modifier = modifier,
            style = applyContentColorIfNeeded(style),
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines
        )
    }
}

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier.None,
    style: TextStyle? = null,
    softWrap: Boolean = DefaultSoftWrap,
    overflow: TextOverflow = DefaultOverflow,
    maxLines: Int = DefaultMaxLines,
    selectable: Boolean = false
) {
    ToggleableSelectable(selectable = selectable) {
        androidx.ui.core.Text(
            text = text,
            modifier = modifier,
            style = applyContentColorIfNeeded(style),
            softWrap = softWrap,
            overflow = overflow,
            maxLines = maxLines
        )
    }
}

@Composable
private fun ToggleableSelectable(
    selectable: Boolean,
    child: @Composable() () -> Unit
) {
    SelectionRegistrarAmbient.Provider(
        value = if (selectable) ambient(SelectionRegistrarAmbient) else NoopSelectionRegistar,
        children = child
    )
}

@Composable
fun applyContentColorIfNeeded(style: TextStyle?): TextStyle {
    var themeStyle = currentTextStyle()
    themeStyle = themeStyle.merge(style).copy(color = themeStyle.color ?: contentColor())
    return themeStyle
}

private object NoopSelectionRegistar : SelectionRegistrar {
    override fun subscribe(selectable: Selectable): Selectable =
        NoopSelectable

    override fun unsubscribe(selectable: Selectable) {
    }

    private object NoopSelectable : Selectable {
        override fun getSelection(
            startPosition: PxPosition,
            endPosition: PxPosition,
            containerLayoutCoordinates: LayoutCoordinates,
            longPress: Boolean
        ): Selection? = null
    }
}

private const val DefaultSoftWrap: Boolean = true
private const val DefaultMaxLines = Int.MAX_VALUE
private val DefaultOverflow: TextOverflow = TextOverflow.Clip