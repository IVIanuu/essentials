package essentials.ui.overlay

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.*
import essentials.*
import essentials.ui.common.ProvideContentColorTextStyle
import essentials.ui.navigation.*
import injekt.*
import com.slack.circuit.foundation.internal.*
import kotlinx.coroutines.*
import soup.compose.material.motion.*
import soup.compose.material.motion.animation.*

@Composable fun Dialog(
  modifier: Modifier = Modifier,
  dismissible: Boolean = true,
  onDismissRequest: () -> Unit = run {
    val navigator = LocalScope.current.navigator
    val key = LocalScope.current.screen
    val scope = rememberCoroutineScope()
    return@run { scope.launch { navigator.pop(key) } }
  },
  icon: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  buttons: (@Composable () -> Unit)? = null,
  content: (@Composable () -> Unit)? = null,
  applyContentPadding: Boolean = true,
) {
  if (!dismissible)
    BackHandler { }

  Box(
    modifier = Modifier
      .pointerInput(true) {
        detectTapGestures { onDismissRequest() }
      }
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.6f))
      .then(modifier),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .safeContentPadding()
        .pointerInput(true) { detectTapGestures { } }
        .wrapContentSize(align = Alignment.Center)
        .padding(all = 32.dp)
    ) {
      Surface(
        modifier = modifier.widthIn(min = 280.dp, max = 356.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 24.dp,
        shape = MaterialTheme.shapes.medium
      ) {
        Box(modifier = Modifier.animateContentSize()) {
          Column {
            val hasHeader = icon != null || title != null

            @Composable fun StyledIcon() {
              CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                content = icon!!
              )
            }

            @Composable fun StyledTitle() {
              ProvideContentColorTextStyle(
                contentColor = MaterialTheme.colorScheme.onSurface,
                textStyle = MaterialTheme.typography.headlineSmall,
                content = title!!
              )
            }

            if (icon != null || title != null)
              Box(
                modifier = Modifier.padding(
                  start = 24.dp,
                  top = 24.dp,
                  end = 24.dp,
                  bottom = if (buttons != null && content == null) 28.dp else 24.dp
                )
              ) {
                if (icon != null && title != null)
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    StyledIcon()
                    Spacer(Modifier.width(16.dp))
                    StyledTitle()
                  }
                else if (icon != null)
                  StyledIcon()
                else if (title != null)
                  StyledTitle()
              }

            if (content != null)
              Box(
                modifier = Modifier
                  .weight(1f, false)
                  .padding(
                    start = if (applyContentPadding) 24.dp else 0.dp,
                    top = if (!hasHeader) 24.dp else 0.dp,
                    end = if (applyContentPadding) 24.dp else 0.dp,
                    bottom = if (buttons == null) 24.dp else 0.dp
                  )
              ) {
                ProvideContentColorTextStyle(
                  contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  textStyle = MaterialTheme.typography.bodyMedium,
                  content = content
                )
              }

            if (buttons != null)
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(
                    start = 8.dp,
                    top = 16.dp,
                    end = 8.dp,
                    bottom = 8.dp
                  ),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                content = { buttons() }
              )
          }
        }
      }
    }
  }
}

interface DialogScreen<T> : OverlayScreen<T>
