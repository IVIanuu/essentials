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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.ui.core.Px
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import com.ivianuu.essentials.ui.compose.core.Axis

@Composable
fun Scroller(
    scrollerPosition: ScrollerPosition = +memo { ScrollerPosition() },
    onScrollPositionChanged: (position: Px, maxPosition: Px) -> Unit = { position, _ ->
        scrollerPosition.value = position
    },
    isScrollable: Boolean = true,
    direction: Axis = Axis.Vertical,
    child: @Composable() () -> Unit
) {
    when (direction) {
        Axis.Vertical -> {
            VerticalScroller(
                scrollerPosition = scrollerPosition,
                onScrollPositionChanged = onScrollPositionChanged,
                isScrollable = isScrollable,
                child = child
            )
        }
        Axis.Horizontal -> {
            HorizontalScroller(
                scrollerPosition = scrollerPosition,
                onScrollPositionChanged = onScrollPositionChanged,
                isScrollable = isScrollable,
                child = child
            )
        }
    }
}