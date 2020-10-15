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

package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.ui.core.ConsumeInsets
import com.ivianuu.essentials.ui.core.InsetsAmbient

@OptIn(InternalLayoutApi::class)
@Composable
fun InsettingScrollableColumn(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(0f),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    reverseScrollDirection: Boolean = false,
    isScrollEnabled: Boolean = true,
    children: @Composable ColumnScope.() -> Unit
) {
    ScrollableColumn(
        modifier = modifier,
        scrollState = scrollState,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        reverseScrollDirection = reverseScrollDirection,
        isScrollEnabled = isScrollEnabled
    ) {
        val insets = InsetsAmbient.current
        Spacer(Modifier.height(insets.top))
        ConsumeInsets(start = false, end = false) {
            children()
        }
        Spacer(Modifier.height(insets.bottom))
    }
}

@OptIn(InternalLayoutApi::class)
@Composable
fun InsettingScrollableRow(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(0f),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    reverseScrollDirection: Boolean = false,
    isScrollEnabled: Boolean = true,
    children: @Composable RowScope.() -> Unit
) {
    ScrollableRow(
        modifier = modifier,
        scrollState = scrollState,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        reverseScrollDirection = reverseScrollDirection,
        isScrollEnabled = isScrollEnabled
    ) {
        val insets = InsetsAmbient.current
        Spacer(Modifier.width(insets.start))
        ConsumeInsets(top = false, bottom = false) {
            children()
        }
        Spacer(Modifier.width(insets.end))
    }
}

@Composable
fun <T> InsettingLazyColumnFor(
    items: List<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    val insets = InsetsAmbient.current
    ConsumeInsets(start = false, end = false) {
        LazyColumnFor(
            items = remember(items) {
                buildList {
                    add(LeadingInsetsItem)
                    addAll(items)
                    add(TrailingInsetsItem)
                }
            },
            modifier = modifier,
            state = state
        ) { item ->
            when (item) {
                LeadingInsetsItem -> {
                    Spacer(Modifier.height(insets.top))
                }
                TrailingInsetsItem -> {
                    Spacer(Modifier.height(insets.bottom))
                }
                else -> itemContent(item as T)
            }
        }
    }
}

@Composable
fun <T> InsettingLazyRowFor(
    items: List<T>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    val insets = InsetsAmbient.current
    ConsumeInsets(top = false, bottom = false) {
        LazyRowFor(
            items = remember(items) {
                buildList {
                    add(LeadingInsetsItem)
                    addAll(items)
                    add(TrailingInsetsItem)
                }
            },
            modifier = modifier,
            state = state
        ) { item ->
            when (item) {
                LeadingInsetsItem -> {
                    Spacer(Modifier.width(insets.start))
                }
                TrailingInsetsItem -> {
                    Spacer(Modifier.width(insets.end))
                }
                else -> itemContent(item as T)
            }
        }
    }
}

private val LeadingInsetsItem = Any()
private val TrailingInsetsItem = Any()
