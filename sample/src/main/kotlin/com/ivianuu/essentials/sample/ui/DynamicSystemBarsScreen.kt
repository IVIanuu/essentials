/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.overlay.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.essentials.ui.util.*
import com.ivianuu.injekt.*

@Provide val dynamicSystemBarsHomeItem = HomeItem("Dynamic system bars") { DynamicSystemBarsScreen() }

class DynamicSystemBarsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide val ui = Ui<DynamicSystemBarsScreen> {
      Box {
        val colors = rememberSaveable {
          ColorPickerPalette.entries
            .fastFilter { it != ColorPickerPalette.BLACK && it != ColorPickerPalette.WHITE }
            .fastFlatMap { it.colors }
            .shuffled()
        }
        EsLazyColumn(contentPadding = PaddingValues(0.dp)) {
          // todo use items once fixed
          items(colors) { color ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(color)
                .systemBarStyle(
                  bgColor = Color.Black.copy(alpha = 0.2f),
                  darkIcons = color.isLight
                )
            )
          }
        }

        EsAppBar(
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
          ),
          title = { Text("Dynamic system bars") }
        )
      }
    }
  }
}
