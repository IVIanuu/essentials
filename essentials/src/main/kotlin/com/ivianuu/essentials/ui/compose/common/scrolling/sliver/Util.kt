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

package com.ivianuu.essentials.ui.compose.common.scrolling.sliver

import androidx.ui.core.Direction
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollDirection

enum class GrowthDirection {
    Forward, Reverse
}

fun ScrollDirection.applyGrowthDirection(growthDirection: GrowthDirection): ScrollDirection =
    when (growthDirection) {
        GrowthDirection.Forward -> this
        GrowthDirection.Reverse -> flip()
    }

fun Direction.applyGrowthDirection(growthDirection: GrowthDirection): Direction =
    when (growthDirection) {
        GrowthDirection.Forward -> this
        GrowthDirection.Reverse -> flip()
    }

fun Direction.flip() = when (this) {
    Direction.UP -> Direction.DOWN
    Direction.RIGHT -> Direction.LEFT
    Direction.DOWN -> Direction.UP
    Direction.LEFT -> Direction.RIGHT
}

fun ScrollDirection.flip() = when (this) {
    ScrollDirection.Idle -> ScrollDirection.Idle
    ScrollDirection.Forward -> ScrollDirection.Reverse
    ScrollDirection.Reverse -> ScrollDirection.Forward
}