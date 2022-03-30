/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.help

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

object HelpKey : Key<Unit>

data class HelpCategory(val title: String? = null, val items: List<HelpItem>)

data class HelpItem(
  val question: String,
  val answer: String,
  val actions: (@Composable () -> Unit)? = null
)

@Provide fun helpUi(categories: List<HelpCategory>) = SimpleKeyUi<HelpKey> {
  var expandedItem: HelpItem? by remember {
    mutableStateOf(
      categories
        .flatMap { it.items }
        .firstOrNull()
    )
  }

  SimpleListScreen(R.string.es_help_title) {
    categories.forEach { category ->
      if (category.title != null) {
        item {
          Subheader { Text(category.title) }
        }
      }

      category.items.forEach { item ->
        item {
          HelpItem(
            item = item,
            isExpanded = expandedItem == item,
            onToggleExpandedClick = {
              expandedItem = if (expandedItem != item) item
              else null
            }
          )
        }
      }
    }
  }
}

@Composable private fun HelpItem(
  item: HelpItem,
  isExpanded: Boolean,
  onToggleExpandedClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .padding(start = 16.dp, top = 16.dp, end = 16.dp)
      .border(
        1.dp,
        LocalContentColor.current.copy(alpha = 0.12f),
        RoundedCornerShape(8.dp)
      )
      .clickable(onClick = onToggleExpandedClick)
      .padding(16.dp)
      .animateContentSize()
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.subtitle1,
        LocalContentAlpha provides ContentAlpha.high
      ) {
        Text(item.question)
      }

      Spacer(Modifier.weight(1f))

      val iconRotation by animateFloatAsState(if (isExpanded) 180f else 0f)
      Icon(
        painterResId = R.drawable.es_ic_expand_more,
        modifier = Modifier
          .size(24.dp)
          .rotate(iconRotation),
        tint = MaterialTheme.colors.primary
      )
    }

    if (isExpanded) {
      CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.body2,
        LocalContentAlpha provides ContentAlpha.medium
      ) {
        Text(
          modifier = Modifier.padding(top = 8.dp),
          text = item.answer
        )
      }

      if (item.actions != null) {
        Row(
          modifier = Modifier.padding(top = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
          verticalAlignment = Alignment.CenterVertically
        ) {
          item.actions.invoke()
        }
      }
    }
  }
}
