/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.systemoverlay

import androidx.compose.ui.Alignment
import com.ivianuu.essentials.Resources
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.util.DisplayRotation
import com.ivianuu.essentials.ui.common.UiRenderer
import com.ivianuu.injekt.Provide

enum class ScreenEdge(
  val titleRes: Int,
  val iconRes: Int,
  val alignment: Alignment,
  val isOnSide: Boolean,
) {
  LEFT(
    titleRes = R.string.es_screen_edge_left,
    iconRes = R.drawable.es_ic_trigger_left,
    alignment = Alignment.TopStart,
    isOnSide = true
  ) {
    override fun rotate(rotation: DisplayRotation) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> this
      DisplayRotation.LANDSCAPE_LEFT -> BOTTOM
      DisplayRotation.PORTRAIT_DOWN -> RIGHT
      DisplayRotation.LANDSCAPE_RIGHT -> TOP
    }

    override fun rotate(rotation: DisplayRotation, position: Float) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> 0f to position
      DisplayRotation.LANDSCAPE_LEFT -> position to 0f
      DisplayRotation.PORTRAIT_DOWN -> 0f to 1f - position
      DisplayRotation.LANDSCAPE_RIGHT -> 1f - position to 0f
    }
  },
  TOP(
    titleRes = R.string.es_screen_edge_top,
    iconRes = R.drawable.es_ic_trigger_top,
    alignment = Alignment.TopStart,
    isOnSide = false
  ) {
    override fun rotate(rotation: DisplayRotation) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> this
      DisplayRotation.LANDSCAPE_LEFT -> LEFT
      DisplayRotation.PORTRAIT_DOWN -> BOTTOM
      DisplayRotation.LANDSCAPE_RIGHT -> RIGHT
    }

    override fun rotate(rotation: DisplayRotation, position: Float) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> position to 0f
      DisplayRotation.LANDSCAPE_LEFT -> 0f to 1f - position
      DisplayRotation.PORTRAIT_DOWN -> 1f - position to 0f
      DisplayRotation.LANDSCAPE_RIGHT -> 0f to position
    }
  },
  RIGHT(
    titleRes = R.string.es_screen_edge_right,
    iconRes = R.drawable.es_ic_trigger_right,
    alignment = Alignment.TopEnd,
    isOnSide = true
  ) {
    override fun rotate(rotation: DisplayRotation) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> this
      DisplayRotation.LANDSCAPE_LEFT -> TOP
      DisplayRotation.PORTRAIT_DOWN -> LEFT
      DisplayRotation.LANDSCAPE_RIGHT -> BOTTOM
    }

    override fun rotate(rotation: DisplayRotation, position: Float) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> 0f to position
      DisplayRotation.LANDSCAPE_LEFT -> position to 0f
      DisplayRotation.PORTRAIT_DOWN -> 0f to 1f - position
      DisplayRotation.LANDSCAPE_RIGHT -> 1f - position to 0f
    }
  },
  BOTTOM(
    titleRes = R.string.es_screen_edge_bottom,
    iconRes = R.drawable.es_ic_trigger_bottom,
    alignment = Alignment.BottomStart,
    isOnSide = false
  ) {
    override fun rotate(rotation: DisplayRotation) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> this
      DisplayRotation.LANDSCAPE_LEFT -> RIGHT
      DisplayRotation.PORTRAIT_DOWN -> TOP
      DisplayRotation.LANDSCAPE_RIGHT -> LEFT
    }

    override fun rotate(rotation: DisplayRotation, position: Float) = when (rotation) {
      DisplayRotation.PORTRAIT_UP -> position to 0f
      DisplayRotation.LANDSCAPE_LEFT -> 0f to 1f - position
      DisplayRotation.PORTRAIT_DOWN -> 1f - position to 0f
      DisplayRotation.LANDSCAPE_RIGHT -> 0f to position
    }
  };

  abstract fun rotate(rotation: DisplayRotation): ScreenEdge

  abstract fun rotate(rotation: DisplayRotation, position: Float): Pair<Float, Float>

  @Provide companion object {
    @Provide fun uiRenderer(resources: Resources) = UiRenderer<ScreenEdge> {
      resources(it.titleRes)
    }
  }
}
