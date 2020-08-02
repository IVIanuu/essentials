package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.ScrollState
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.ScrollableRow
import androidx.ui.foundation.rememberScrollState
import androidx.ui.layout.Arrangement
import androidx.ui.layout.ColumnScope
import androidx.ui.layout.RowScope
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.layout.width
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
