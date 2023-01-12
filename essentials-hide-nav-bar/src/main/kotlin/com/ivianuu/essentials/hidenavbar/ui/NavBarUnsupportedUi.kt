/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.hidenavbar.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.hidenavbar.R
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide

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
          onClick = openMoreInfos
        ) {
          Text(R.string.es_more_infos)
        }

        Button(
          modifier = Modifier
            .width(200.dp)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
          onClick = openRootMethod
        ) {
          Text(R.string.es_root_method)
        }
      }
    }
  }
}

data class NavBarUnsupportedModel(val openMoreInfos: () -> Unit, val openRootMethod: () -> Unit)

context(KeyUiContext<NavBarUnsupportedKey>) @Provide fun navBarUnsupportedModel() = Model {
  NavBarUnsupportedModel(
    openMoreInfos = action {
      navigator.push(
        UrlKey(
          "https://www.xda-developers.com/google-confirms-overscan-gone-android-11-crippling-third-party-gesture-apps/"
        )
      )
    },
    openRootMethod = action {
      navigator.push(
        UrlKey(
          "https://forum.xda-developers.com/t/how-to-remove-nav-bar-in-android-11.4190469/"
        )
      )
    }
  )
}
