/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*

object NavBarUnsupportedKey : Key<Unit>

@Provide val navBarUnsupportedUi = ModelKeyUi<NavBarUnsupportedKey, NavBarUnsupportedModel> {
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_nav_bar_unsupported_title) }) }
  ) {
    Column {
      Text(
        modifier = Modifier.padding(16.dp),
        textResId = R.string.es_nav_bar_unsupported_content,
        style = MaterialTheme.typography.body2
      )

      Row {
        Button(
          modifier = Modifier
            .width(200.dp)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
          onClick = model.openMoreInfos
        ) {
          Text(R.string.es_more_infos)
        }

        Button(
          modifier = Modifier
            .width(200.dp)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
          onClick = model.openRootMethod
        ) {
          Text(R.string.es_root_method)
        }
      }
    }
  }
}

data class NavBarUnsupportedModel(val openMoreInfos: () -> Unit, val openRootMethod: () -> Unit)

@Provide @Composable fun navBarUnsupportedModel(
  ctx: KeyUiContext<NavBarUnsupportedKey>
) = NavBarUnsupportedModel(
  openMoreInfos = action {
    ctx.navigator.push(
      UrlKey(
        "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
      )
    )
  },
  openRootMethod = action {
    ctx.navigator.push(
      UrlKey(
        "https://forum.xda-developers.com/t/how-to-remove-nav-bar-in-android-11.4190469/"
      )
    )
  }
)
