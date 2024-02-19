/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import androidx.compose.foundation.layout.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.util.*

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
    edge = rotatedEdge
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
