package essentials.ui.common

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*

@Composable fun ProvideContentColorTextStyle(
  contentColor: Color,
  textStyle: TextStyle,
  content: @Composable () -> Unit
) {
  val mergedStyle = LocalTextStyle.current.merge(textStyle)
  CompositionLocalProvider(
    LocalContentColor provides contentColor,
    LocalTextStyle provides mergedStyle,
    content = content
  )
}
