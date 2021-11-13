/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.ui.animation.transition

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.boundsInWindow
import com.ivianuu.essentials.lerp
import com.ivianuu.essentials.time.milliseconds
import com.ivianuu.essentials.ui.animation.ContentAnimationElementKey
import kotlin.math.hypot

fun CircularRevealStackTransition(
  centerElementKey: Any,
  spec: AnimationSpec<Float> = defaultAnimationSpec(500.milliseconds, easing = FastOutSlowInEasing)
): StackTransition = {
  val clipTarget = if (isPush) toElementModifier(ContentAnimationElementKey)
  else fromElementModifier(ContentAnimationElementKey)
  if (isPush) clipTarget?.value = Modifier.alpha(0f)
  attachTo()

  val center: Offset
  val startRadius: Float
  val endRadius: Float
  if (isPush) {
    val toElementCoords = toElementModifier(ContentAnimationElementKey)!!.awaitLayoutCoordinates()
    val centerElementCoords = fromElementModifier(centerElementKey)!!.awaitLayoutCoordinates()
    center = centerElementCoords.boundsInWindow().center
    startRadius = 0f
    endRadius = hypot(
      toElementCoords.size.width.toFloat(),
      toElementCoords.size.height.toFloat()
    )
  } else {
    val toElementCoords =
      fromElementModifier(ContentAnimationElementKey)!!.awaitLayoutCoordinates()
    val centerElementCoords = toElementModifier(centerElementKey)!!.awaitLayoutCoordinates()
    center = centerElementCoords.boundsInWindow().center
    startRadius = hypot(
      toElementCoords.size.width.toFloat(),
      toElementCoords.size.height.toFloat()
    )
    endRadius = 0f
  }

  val path = Path()
  animate(spec) { value ->
    clipTarget?.value = Modifier
      .drawWithContent {
        clipPath(
          path = path.apply {
            reset()
            addOval(
              Rect(
                center,
                lerp(startRadius, endRadius, value)
              )
            )
          }
        ) {
          this@drawWithContent.drawContent()
        }
      }
  }
}
