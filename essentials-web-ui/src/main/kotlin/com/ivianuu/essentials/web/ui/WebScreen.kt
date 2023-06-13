/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.web.ui

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.compose.getValue
import com.ivianuu.essentials.compose.refOf
import com.ivianuu.essentials.compose.setValue
import com.ivianuu.essentials.ui.layout.navigationBarsPadding
import com.ivianuu.essentials.ui.layout.systemBarsPadding
import com.ivianuu.essentials.ui.material.AppBarStyle
import com.ivianuu.essentials.ui.material.LocalAppBarStyle
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.UrlScreen
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.ui.systembars.systemBarStyle
import com.ivianuu.injekt.Provide

class WebScreen(val title: String, val url: String) : Screen<Unit>

@Provide fun webUi(
  navigator: Navigator,
  screen: WebScreen
) = Ui<WebScreen, Unit> {
  var webViewRef: WebView? by remember { refOf(null) }
  DisposableEffect(true) {
    onDispose {
      webViewRef?.destroy()
    }
  }
  Scaffold(
    topBar = { TopAppBar(title = { Text(screen.title) }) },
    bottomBar = {
      val backgroundColor = when (LocalAppBarStyle.current) {
        AppBarStyle.PRIMARY -> MaterialTheme.colors.primary
        AppBarStyle.SURFACE -> MaterialTheme.colors.surface
      }
      Surface(
        modifier = Modifier.systemBarStyle(backgroundColor),
        color = backgroundColor,
        elevation = 8.dp
      ) {
        BottomAppBar(
          modifier = Modifier.navigationBarsPadding(),
          elevation = 0.dp,
          backgroundColor = backgroundColor
        ) {
          IconButton(onClick = { webViewRef!!.goBack() }) {
            Icon(R.drawable.es_ic_arrow_back_ios_new)
          }
          IconButton(onClick = { webViewRef!!.reload() }) {
            Icon(R.drawable.es_ic_refresh)
          }
          IconButton(onClick = action { navigator.push(UrlScreen(webViewRef!!.url!!)) }) {
            Icon(R.drawable.es_ic_open_in_browser)
          }
        }
      }
    }
  ) {
    AndroidView(
      modifier = Modifier
        .systemBarsPadding()
        .fillMaxSize(),
      factory = { WebView(it) }
    ) { webView ->
      webViewRef = webView
      webView.settings.javaScriptEnabled = true
      webView.loadUrl(screen.url)
      webView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
          view: WebView?,
          request: WebResourceRequest?
        ): Boolean = false
      }
    }
  }
}
