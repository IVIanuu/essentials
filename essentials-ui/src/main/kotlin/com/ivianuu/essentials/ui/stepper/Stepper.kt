package com.ivianuu.essentials.ui.stepper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.R
import com.ivianuu.essentials.ui.material.VerticalDivider
import com.ivianuu.essentials.ui.material.guessingContentColorFor

@Composable fun Step(
  step: Int,
  isCurrent: Boolean,
  isCompleted: Boolean,
  onClick: () -> Unit,
  title: @Composable () -> Unit,
  content: (@Composable () -> Unit)? = null,
  actions: @Composable () -> Unit
) {
  Column(
    modifier = Modifier.fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 24.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth()
        .height(48.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      val backgroundColor = if (isCurrent || isCompleted) MaterialTheme.colors.secondary
      else LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
      Box(
        modifier = Modifier
          .size(24.dp)
          .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        CompositionLocalProvider(
          LocalContentColor provides guessingContentColorFor(backgroundColor)
        ) {
          if (isCompleted) {
            Icon(
              modifier = Modifier.padding(4.dp),
              painterResId = R.drawable.es_ic_done
            )
          } else {
            Text(
              text = step.toString(),
              style = MaterialTheme.typography.caption
            )
          }
        }
      }

      Spacer(Modifier.width(16.dp))

      CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.subtitle1,
        LocalContentAlpha provides ContentAlpha.high,
        content = title
      )
    }

    Spacer(Modifier.height(8.dp))

    Row {
      VerticalDivider(
        modifier = Modifier
          .padding(start = 24.dp)
      )

      Spacer(Modifier.width(40.dp))

      if (isCurrent) {
        Column {
          if (content != null) {
            CompositionLocalProvider(
              LocalTextStyle provides MaterialTheme.typography.body2,
              LocalContentAlpha provides ContentAlpha.medium,
              content = content
            )

            Spacer(Modifier.padding(bottom = 16.dp))
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            actions()
          }
        }
      }
    }
  }
}
