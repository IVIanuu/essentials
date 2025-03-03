/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.systemoverlay

import androidx.compose.ui.*
import essentials.android.R
import essentials.util.*

enum class ScreenEdge(
  val title: String,
  val iconRes: Int,
  val alignment: Alignment,
  val isOnSide: Boolean,
) {
  LEFT(
    title = "Left",
    iconRes = R.drawable.ic_trigger_left,
    alignment = Alignment.TopStart,
    isOnSide = true
  ) {
    override fun rotate(rotation: ScreenRotation) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> this
      ScreenRotation.LANDSCAPE_LEFT -> BOTTOM
      ScreenRotation.PORTRAIT_DOWN -> RIGHT
      ScreenRotation.LANDSCAPE_RIGHT -> TOP
    }

    override fun rotate(rotation: ScreenRotation, position: Float) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> 0f to position
      ScreenRotation.LANDSCAPE_LEFT -> position to 0f
      ScreenRotation.PORTRAIT_DOWN -> 0f to 1f - position
      ScreenRotation.LANDSCAPE_RIGHT -> 1f - position to 0f
    }
  },
  TOP(
    title = "Top",
    iconRes = R.drawable.ic_trigger_top,
    alignment = Alignment.TopStart,
    isOnSide = false
  ) {
    override fun rotate(rotation: ScreenRotation) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> this
      ScreenRotation.LANDSCAPE_LEFT -> LEFT
      ScreenRotation.PORTRAIT_DOWN -> BOTTOM
      ScreenRotation.LANDSCAPE_RIGHT -> RIGHT
    }

    override fun rotate(rotation: ScreenRotation, position: Float) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> position to 0f
      ScreenRotation.LANDSCAPE_LEFT -> 0f to 1f - position
      ScreenRotation.PORTRAIT_DOWN -> 1f - position to 0f
      ScreenRotation.LANDSCAPE_RIGHT -> 0f to position
    }
  },
  RIGHT(
    title = "Right",
    iconRes = R.drawable.ic_trigger_right,
    alignment = Alignment.TopEnd,
    isOnSide = true
  ) {
    override fun rotate(rotation: ScreenRotation) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> this
      ScreenRotation.LANDSCAPE_LEFT -> TOP
      ScreenRotation.PORTRAIT_DOWN -> LEFT
      ScreenRotation.LANDSCAPE_RIGHT -> BOTTOM
    }

    override fun rotate(rotation: ScreenRotation, position: Float) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> 0f to position
      ScreenRotation.LANDSCAPE_LEFT -> position to 0f
      ScreenRotation.PORTRAIT_DOWN -> 0f to 1f - position
      ScreenRotation.LANDSCAPE_RIGHT -> 1f - position to 0f
    }
  },
  BOTTOM(
    title = "Bottom",
    iconRes = R.drawable.ic_trigger_bottom,
    alignment = Alignment.BottomStart,
    isOnSide = false
  ) {
    override fun rotate(rotation: ScreenRotation) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> this
      ScreenRotation.LANDSCAPE_LEFT -> RIGHT
      ScreenRotation.PORTRAIT_DOWN -> TOP
      ScreenRotation.LANDSCAPE_RIGHT -> LEFT
    }

    override fun rotate(rotation: ScreenRotation, position: Float) = when (rotation) {
      ScreenRotation.PORTRAIT_UP -> position to 0f
      ScreenRotation.LANDSCAPE_LEFT -> 0f to 1f - position
      ScreenRotation.PORTRAIT_DOWN -> 1f - position to 0f
      ScreenRotation.LANDSCAPE_RIGHT -> 0f to position
    }
  };

  abstract fun rotate(rotation: ScreenRotation): ScreenEdge

  abstract fun rotate(rotation: ScreenRotation, position: Float): Pair<Float, Float>
}
