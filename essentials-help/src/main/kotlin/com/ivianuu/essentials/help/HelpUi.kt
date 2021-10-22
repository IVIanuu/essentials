/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.help

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.Subheader
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.injekt.Provide

object HelpKey : Key<Unit>

data class HelpCategory(val title: String?, val items: List<HelpItem>)

data class HelpItem(
  val question: String,
  val answer: String,
  val actions: (@Composable () -> Unit)? = null
)

@Provide fun helpUi(categories: List<HelpCategory>): KeyUi<HelpKey> = {
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
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
        ProvideTextStyle(MaterialTheme.typography.subtitle1) {
          Text(item.question)
        }
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
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        ProvideTextStyle(MaterialTheme.typography.body2) {
          Text(
            modifier = Modifier.padding(top = 8.dp),
            text = item.answer
          )
        }
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
