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
import androidx.compose.Immutable
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import androidx.ui.core.LayoutCoordinates
import androidx.ui.core.Modifier
import androidx.ui.core.selection.Selectable
import androidx.ui.core.selection.Selection
import androidx.ui.core.selection.SelectionRegistrar
import androidx.ui.core.selection.SelectionRegistrarAmbient
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.currentTextStyle
import androidx.ui.res.stringResource
import androidx.ui.text.AnnotatedString
import androidx.ui.text.TextStyle
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.PxPosition

@Immutable
data class TextComposableStyle(
    val uppercase: Boolean,
    val softWrap: Boolean,
    val overflow: TextOverflow,
    val maxLines: Int,
    val selectable: Boolean,
    val modifier: Modifier
)

@Composable
fun DefaultTextComposableStyle(
    uppercase: Boolean = false,
    softWrap: Boolean = DefaultSoftWrap,
    overflow: TextOverflow = DefaultOverflow,
    maxLines: Int = DefaultMaxLines,
    selectable: Boolean = false
) = TextComposableStyle(
    uppercase = uppercase,
    softWrap = softWrap,
    overflow = overflow,
    maxLines = maxLines,
    selectable = selectable,
    modifier = Modifier.None
)

val TextComposableStyleAmbient =
    staticAmbientOf<TextComposableStyle>()

@Composable
fun Text(
    textRes: Int,
    modifier: Modifier = Modifier.None,
    style: TextComposableStyle = TextComposableStyleAmbient.currentOrElse { DefaultTextComposableStyle() },
    textStyle: TextStyle = currentTextStyle()
) {
    Text(
        text = stringResource(textRes),
        modifier = modifier,
        style = style,
        textStyle = textStyle
    )
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier.None,
    style: TextComposableStyle = TextComposableStyleAmbient.currentOrElse { DefaultTextComposableStyle() },
    textStyle: TextStyle = currentTextStyle()
) {
    ToggleableSelectable(selectable = style.selectable) {
        androidx.ui.foundation.Text(
            text = if (style.uppercase) text.toUpperCase() else text,
            modifier = style.modifier.plus(modifier),
            style = ensureColor(textStyle),
            softWrap = style.softWrap,
            overflow = style.overflow,
            maxLines = style.maxLines
        )
    }
}

@Composable
fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier.None,
    style: TextComposableStyle = TextComposableStyleAmbient.currentOrElse { DefaultTextComposableStyle() },
    textStyle: TextStyle = currentTextStyle()
) {
    ToggleableSelectable(selectable = style.selectable) {
        androidx.ui.foundation.Text(
            text = if (style.uppercase) text.copy(text = text.text.toUpperCase()) else text,
            modifier = modifier,
            style = ensureColor(textStyle),
            softWrap = style.softWrap,
            overflow = style.overflow,
            maxLines = style.maxLines
        )
    }
}

@Composable
private fun ToggleableSelectable(
    selectable: Boolean,
    children: @Composable () -> Unit
) {
    Providers(
        SelectionRegistrarAmbient provides if (selectable) SelectionRegistrarAmbient.current else NoopSelectionRegistrar,
        children = children
    )
}

@Composable
internal fun ensureColor(style: TextStyle): TextStyle =
    if (style.color != null) style else style.copy(color = contentColor())

private object NoopSelectionRegistrar : SelectionRegistrar {
    override fun subscribe(selectable: Selectable): Selectable =
        NoopSelectable

    override fun onPositionChange() {
    }

    override fun unsubscribe(selectable: Selectable) {
    }

    private object NoopSelectable : Selectable {
        override fun getSelection(
            startPosition: PxPosition,
            endPosition: PxPosition,
            containerLayoutCoordinates: LayoutCoordinates,
            longPress: Boolean,
            previousSelection: Selection?,
            isStartHandle: Boolean
        ): Selection? = null

        override fun getText(): AnnotatedString = AnnotatedString("")

        override fun getHandlePosition(selection: Selection, isStartHandle: Boolean): PxPosition = PxPosition.Origin
        override fun getLayoutCoordinates(): LayoutCoordinates? = null
    }
}

private const val DefaultSoftWrap: Boolean = true
private const val DefaultMaxLines = Int.MAX_VALUE
private val DefaultOverflow: TextOverflow = TextOverflow.Clip
