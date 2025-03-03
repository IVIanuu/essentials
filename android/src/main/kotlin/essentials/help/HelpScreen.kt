/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.help

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

class HelpScreen(val categories: List<HelpCategory>) : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(screen: HelpScreen) = Ui<HelpScreen> {
      var expandedItem: HelpItem? by remember {
        mutableStateOf(
          screen.categories
            .fastFlatMap { it.items }
            .firstOrNull()
        )
      }

      EsScaffold(topBar = { EsAppBar { Text("Help") } }) {
        EsLazyColumn {
          screen.categories.fastForEach { category ->
            if (category.title != null) {
              item {
                Subheader { Text(category.title) }
              }
            }

            items(category.items) { item ->
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
          .padding(start = 8.dp, top = 8.dp, end = 8.dp)
          .border(
            1.dp,
            LocalContentColor.current.copy(alpha = 0.12f),
            RoundedCornerShape(8.dp)
          )
          .clip(RoundedCornerShape(8.dp))
          .clickable(onClick = onToggleExpandedClick)
          .padding(16.dp)
          .animateContentSize()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          ProvideContentColorTextStyle(
            contentColor = MaterialTheme.colorScheme.onSurface,
            textStyle = MaterialTheme.typography.titleLarge
          ) {
            Text(item.question)
          }

          Spacer(Modifier.weight(1f))

          Spacer(Modifier.width(8.dp))

          val iconRotation by animateFloatAsState(if (isExpanded) 180f else 0f)
          Icon(
            Icons.Default.ExpandMore,
            modifier = Modifier
              .requiredSize(24.dp)
              .rotate(iconRotation),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
          )
        }

        if (isExpanded) {
          Spacer(Modifier.height(8.dp))

          ProvideContentColorTextStyle(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            textStyle = MaterialTheme.typography.bodyMedium
          ) {
            Text(text = item.answer)
          }

          if (item.actions != null) {
            Spacer(Modifier.height(8.dp))

            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
              verticalAlignment = Alignment.CenterVertically
            ) {
              item.actions.invoke()
            }
          }
        }
      }
    }
  }
}

data class HelpCategory(val title: String? = null, val items: List<HelpItem>) {
  companion object {
    @Provide val defaultCategories get() = emptyList<HelpCategory>()
  }
}

data class HelpItem(
  val question: String,
  val answer: String,
  val actions: (@Composable () -> Unit)? = null
)
