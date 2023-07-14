package com.ivianuu.essentials.ui.material

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import com.ivianuu.injekt.Provide

@Composable fun rememberTopBarScrollState(): TopBarScrollState =
  rememberSaveable(saver = TopBarScrollState.Saver) { TopBarScrollState() }

class TopBarScrollState(
  maxHeightOffset: Float = -Float.MAX_VALUE,
  heightOffset: Float = 0f
) : NestedScrollConnection {
  var maxHeightOffset by mutableStateOf(maxHeightOffset)

  private var _heightOffset = mutableStateOf(heightOffset)
  var heightOffset: Float
    get() = _heightOffset.value
    set(newOffset) {
      _heightOffset.value = newOffset.coerceIn(
        minimumValue = maxHeightOffset,
        maximumValue = 0f
      )
    }

  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    val prevHeightOffset = heightOffset
    heightOffset += available.y
    return if (prevHeightOffset != heightOffset) {
      // We're in the middle of top app bar collapse or expand.
      // Consume only the scroll on the Y axis.
      available.copy(x = 0f)
    } else {
      Offset.Zero
    }
  }

  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource
  ): Offset {
    heightOffset += consumed.y
    return Offset.Zero
  }

  @Provide companion object {
    val Saver: Saver<TopBarScrollState, *> = listSaver(
      save = { listOf(it.maxHeightOffset, it.heightOffset) },
      restore = { TopBarScrollState(it[0], it[1]) }
    )
  }
}
