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

package com.ivianuu.essentials.web.ui

import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

data class WebKey(val title: String, val url: String) : Key<Nothing>

@Provide fun webUi(key: WebKey, navigator: Navigator): KeyUi<WebKey> = {
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
              Icon(painterResource(R.drawable.es_ic_arrow_back_ios_new), null)
            }
            IconButton(onClick = { webViewRef!!.reload() }) {
              Icon(painterResource(R.drawable.es_ic_refresh), null)
            }
            val scope = rememberCoroutineScope()
            IconButton(onClick = {
              scope.launch {
                navigator.push(UrlKey(webViewRef!!.url!!))
              }
            }) {
              Icon(painterResource(R.drawable.es_ic_open_in_browser), null)
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
