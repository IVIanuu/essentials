package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.foundation.lazy.LazyRowItems
import androidx.ui.layout.ColumnScope
import androidx.ui.layout.RowScope
import androidx.ui.layout.Spacer
import androidx.ui.layout.height
import androidx.ui.layout.width
import com.ivianuu.essentials.ui.core.InsetsAmbient

@Composable
fun InsettingVerticalScroller(
    scrollerPosition: ScrollerPosition = ScrollerPosition(),
    modifier: Modifier = Modifier,
    isScrollable: Boolean = true,
    children: @Composable ColumnScope.() -> Unit
) {
    VerticalScroller(scrollerPosition, modifier, isScrollable) {
        val insets = InsetsAmbient.current
        Spacer(Modifier.height(insets.systemBars.top))
        children()
        Spacer(Modifier.height(insets.systemBars.bottom))
    }
}

@Composable
fun InsettingHorizontalScroller(
    scrollerPosition: ScrollerPosition = ScrollerPosition(),
    modifier: Modifier = Modifier,
    isScrollable: Boolean = true,
    children: @Composable RowScope.() -> Unit
) {
    HorizontalScroller(scrollerPosition, modifier, isScrollable) {
        val insets = InsetsAmbient.current
        Spacer(Modifier.width(insets.systemBars.start))
        children()
        Spacer(Modifier.width(insets.systemBars.end))
    }
}

@Composable
fun <T> InsettingLazyColumnItems(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
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
                val insets = InsetsAmbient.current
                Spacer(Modifier.height(insets.systemBars.top))
            }
            TrailingInsetsItem -> {
                val insets = InsetsAmbient.current
                Spacer(Modifier.height(insets.systemBars.bottom))
            }
            else -> itemContent(item as T)
        }
    }
}

@Composable
fun <T> InsettingRowColumnItems(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
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
                val insets = InsetsAmbient.current
                Spacer(Modifier.width(insets.systemBars.start))
            }
            TrailingInsetsItem -> {
                val insets = InsetsAmbient.current
                Spacer(Modifier.width(insets.systemBars.end))
            }
            else -> itemContent(item as T)
        }
    }
}

private val LeadingInsetsItem = Any()
private val TrailingInsetsItem = Any()
