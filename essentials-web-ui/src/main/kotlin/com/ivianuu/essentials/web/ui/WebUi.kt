/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.web.ui

import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.insets.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.systembars.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

data class WebKey(val title: String, val url: String) : Key<Unit>

@Provide fun webUi(key: WebKey, navigator: Navigator) = KeyUi<WebKey> {
  var webViewRef: WebView? by remember { refOf(null) }
  DisposableEffect(true) {
    onDispose {
      webViewRef?.destroy()
    }
  }
  Scaffold(
    topBar = { TopAppBar(title = { Text(key.title) }) },
    bottomBar = {
      val backgroundColor = when (LocalAppBarStyle.current) {
        AppBarStyle.PRIMARY -> MaterialTheme.colors.primary
        AppBarStyle.SURFACE -> MaterialTheme.colors.surface
      }
      Surface(color = backgroundColor, elevation = 8.dp) {
        InsetsPadding(
          modifier = Modifier
            .systemBarStyle(backgroundColor),
          left = false,
          top = false,
          right = false
        ) {
          BottomAppBar(
            elevation = 0.dp,
            backgroundColor = backgroundColor
          ) {
            IconButton(onClick = { webViewRef!!.goBack() }) {
              Icon(R.drawable.es_ic_arrow_back_ios_new)
            }
            IconButton(onClick = { webViewRef!!.reload() }) {
              Icon(R.drawable.es_ic_refresh)
            }
            val scope = rememberCoroutineScope()
            IconButton(onClick = {
              scope.launch {
                navigator.push(UrlKey(webViewRef!!.url!!))
              }
            }) {
              Icon(R.drawable.es_ic_open_in_browser)
            }
          }
        }
      }
    }
  ) {
    InsetsPadding {
      AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { WebView(it) }
      ) { webView ->
        webViewRef = webView
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(key.url)
        webView.webViewClient = object : WebViewClient() {
          override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
          ): Boolean = false
        }
      }
    }
  }
}
