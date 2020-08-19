package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import com.ivianuu.essentials.ui.core.ConsumeInsets
import com.ivianuu.essentials.ui.core.InsetsAmbient

@Composable
fun InsettingScrollableColumn(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(0f),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalGravity: Alignment.Horizontal = Alignment.Start,
    reverseScrollDirection: Boolean = false,
    isScrollEnabled: Boolean = true,
    children: @Composable ColumnScope.() -> Unit
) {
    ScrollableColumn(
        modifier = modifier,
        scrollState = scrollState,
        verticalArrangement = verticalArrangement,
        horizontalGravity = horizontalGravity,
        reverseScrollDirection = reverseScrollDirection,
        isScrollEnabled = isScrollEnabled
    ) {
        val insets = InsetsAmbient.current
        Spacer(Modifier.height(insets.systemBars.top))
        ConsumeInsets(left = false, right = false) {
            children()
        }
        Spacer(Modifier.height(insets.systemBars.bottom))
    }
}

@Composable
fun InsettingScrollableRow(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(0f),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalGravity: Alignment.Vertical = Alignment.Top,
    reverseScrollDirection: Boolean = false,
    isScrollEnabled: Boolean = true,
    children: @Composable RowScope.() -> Unit
) {
    ScrollableRow(
        modifier = modifier,
        scrollState = scrollState,
        horizontalArrangement = horizontalArrangement,
        verticalGravity = verticalGravity,
        reverseScrollDirection = reverseScrollDirection,
        isScrollEnabled = isScrollEnabled
    ) {
        val insets = InsetsAmbient.current
        Spacer(Modifier.width(insets.systemBars.start))
        ConsumeInsets(top = false, bottom = false) {
            children()
        }
        Spacer(Modifier.width(insets.systemBars.end))
    }
}

@Composable
fun <T> InsettingLazyColumnItems(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    val insets = InsetsAmbient.current
    ConsumeInsets(left = false, right = false) {
        LazyColumnItems(
            items = remember(items) {
                buildList {
                    add(LeadingInsetsItem)
                    addAll(items)
                    add(TrailingInsetsItem)
                }
            },
            modifier = modifier
        ) { item ->
            when (item) {
                LeadingInsetsItem -> {
                    Spacer(Modifier.height(insets.systemBars.top))
                }
                TrailingInsetsItem -> {
                    Spacer(Modifier.height(insets.systemBars.bottom))
                }
                else -> itemContent(item as T)
            }
        }
    }
}

@Composable
fun <T> InsettingRowColumnItems(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    val insets = InsetsAmbient.current
    ConsumeInsets(top = false, bottom = false) {
        LazyRowItems(
            items = remember(items) {
                buildList {
                    add(LeadingInsetsItem)
                    addAll(items)
                    add(TrailingInsetsItem)
                }
            },
            modifier = modifier
        ) { item ->
            when (item) {
                LeadingInsetsItem -> {
                    Spacer(Modifier.width(insets.systemBars.start))
                }
                TrailingInsetsItem -> {
                    Spacer(Modifier.width(insets.systemBars.end))
                }
                else -> itemContent(item as T)
            }
        }
    }
}

private val LeadingInsetsItem = Any()
private val TrailingInsetsItem = Any()
