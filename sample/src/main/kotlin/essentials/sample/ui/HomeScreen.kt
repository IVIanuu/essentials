/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalComposeUiApi::class)

package essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.overlay.*
import essentials.util.*
import injekt.*

@Provide class HomeScreen : RootScreen

@Provide @Composable fun HomeUi(
  itemsFactory: () -> List<HomeItem>,
  context: ScreenContext<HomeScreen> = inject,
  showToast: showToast
): Ui<HomeScreen> {
  val finalItems = remember { itemsFactory().sortedBy { it.title } }
  EsScaffold(
    topBar = {
      EsAppBar(
        title = { Text("Home") },
        actions = {
          BottomSheetLauncherButton {
            Subheader { Text("Example sheet") }

            SectionListItem(
              onClick = action {
                showToast("Clicked")
                dismiss()
              },
              title = { Text("Test") },
              trailing = { Icon(Icons.Default.Add, null) }
            )
          }
        }
      )
    }
  ) {
    EsLazyColumn {
      finalItems
        .groupBy { it.title.first().uppercase() }
        .toList()
        .sortedBy { it.first }
        .forEach { (title, items) ->
          section(header = {
            SectionHeader { Text(title) }
          }) {
            sectionItemsIndexed(items) { index, item, _ ->
              val color = rememberSaveable(item) {
                DefaultColorPalette
                  .shuffled()
                  .first()
              }

              HomeItem(
                item = item,
                color = color,
                onClick = action {
                  navigator().push(
                    item.screenFactoryWithIndex?.invoke(index, color)
                      ?: item.screenFactory(color)
                  )
                }
              )
            }
          }

        }
    }
  }
}

@Composable private fun HomeItem(
  color: Color,
  item: HomeItem,
  sectionType: SectionType = inject,
  navigator: Navigator = inject,
  onClick: () -> Unit
) {
  SectionListItem(
    onClick = onClick,
    title = { Text(item.title) },
    description = { Text(Strings.Text) },
    trailing = {
      Box(
        modifier = with(LocalScreenAnimationScope.current) {
          Modifier
            .size(40.dp)
            .background(color, CircleShape)
        }
      )
    }
  )
}

data class HomeItem(
  val title: String,
  val screenFactoryWithIndex: ((Int, Color) -> Screen<*>)? = null,
  val screenFactory: (Color) -> Screen<*>
)
