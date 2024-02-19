package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Composable fun Modifier.clickableWithPosition(
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  indication: Indication? = LocalIndication.current,
  enabled: Boolean = true,
  onClick: (Offset) -> Unit
): Modifier {
  val scope = rememberCoroutineScope()
  return clickable(interactionSource, indication, enabled, null, null) {
    scope.launch(start = CoroutineStart.UNDISPATCHED) {
      val position = interactionSource.interactions
        .mapNotNull {
          when (it) {
            is PressInteraction.Press -> it.pressPosition
            is PressInteraction.Release -> it.press.pressPosition
            is PressInteraction.Cancel -> it.press.pressPosition
            else -> null
          }
        }
        .first()

      onClick(position)
    }
  }
}
