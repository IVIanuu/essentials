package com.ivianuu.essentials.ui.common

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@Composable fun Modifier.clickableWithPosition(
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  indication: Indication? = LocalIndication.current,
  enabled: Boolean = true,
  onClick: ((Offset) -> Unit)? = null
): Modifier {
  val scope = rememberCoroutineScope()
  return clickable(interactionSource, indication, enabled, null, null) {
    if (onClick == null) return@clickable
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
