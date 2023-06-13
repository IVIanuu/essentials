/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.popup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.LocalScope
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.pop

@Composable fun PopupMenuItem(
  onSelected: () -> Unit,
  content: @Composable () -> Unit,
) {
  val navigator = LocalScope.current.service<Navigator>()
  val key = LocalScope.current.service<Key<*>>()

  Box(
    modifier = Modifier.widthIn(min = 200.dp)
      .height(48.dp)
      .clickable(onClick = action {
        navigator.pop(key)
        onSelected()
      }),
    contentAlignment = Alignment.CenterStart
  ) {
    Box(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      content()
    }
  }
}
