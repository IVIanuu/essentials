/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.screenstate.DisplayRotation
import com.ivianuu.essentials.ui.layout.align

data class TriggerGeometry(
  val width: Int,
  val height: Int,
  val horizontalMargin: Int,
  val verticalMargin: Int,
  val rotation: DisplayRotation,
  val edge: ScreenEdge
)

fun TriggerGeometry(
  edge: ScreenEdge,
  size: Float,
  sensitivity: Float,
  location: Float,
  rotate: Boolean,
  containerSize: IntSize,
  rotation: DisplayRotation,
  density: Density
): TriggerGeometry {
  val rotation = if (rotate) {
    DisplayRotation.PORTRAIT_UP
  } else {
    rotation
  }

  val rotatedEdge = edge.rotate(rotation)

  val sensitivityPx = with(density) {
    100.dp.roundToPx() * sensitivity
  }.toInt()

  val width: Int
  val height: Int
  val horizontalMargin: Int
  val verticalMargin: Int
  if (rotatedEdge.isOnSide) {
    width = sensitivityPx
    height = (containerSize.height * size).toInt()
    horizontalMargin = 0
    verticalMargin =
      ((containerSize.height - height) * edge.rotate(rotation, location).second).toInt()
  } else {
    width = (containerSize.width * size).toInt()
    height = sensitivityPx
    horizontalMargin =
      ((containerSize.width - width) * edge.rotate(rotation, location).first).toInt()
    verticalMargin = 0
  }

  return TriggerGeometry(
    width = width,
    height = height,
    horizontalMargin = horizontalMargin,
    verticalMargin = verticalMargin,
    rotation = rotation,
    edge = edge
  )
}

fun Modifier.triggerGeometry(geometry: TriggerGeometry) = composed {
  with(LocalDensity.current) {
    align(geometry.edge.alignment)
      .size(geometry.width.toDp(), geometry.height.toDp())
      .offset(
        geometry.horizontalMargin.toDp(),
        geometry.verticalMargin.toDp()
      )
  }
}
