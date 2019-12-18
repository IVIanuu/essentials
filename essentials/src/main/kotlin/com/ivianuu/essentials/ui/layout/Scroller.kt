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

package com.ivianuu.essentials.ui.layout

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import com.ivianuu.essentials.ui.core.Axis

@Composable
fun Scroller(
    direction: Axis,
    scrollerPosition: ScrollerPosition = remember { ScrollerPosition() },
    modifier: Modifier = Modifier.None,
    isScrollable: Boolean = true,
    child: @Composable() () -> Unit
) {
    when (direction) {
        Axis.Horizontal -> {
            HorizontalScroller(
                scrollerPosition = scrollerPosition,
                modifier = modifier,
                isScrollable = isScrollable,
                child = child
            )
        }
        Axis.Vertical -> {
            VerticalScroller(
                scrollerPosition = scrollerPosition,
                modifier = modifier,
                isScrollable = isScrollable,
                child = child
            )
        }
    }
}
