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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ivianuu.essentials.ui.common.getValue
import com.ivianuu.essentials.ui.common.refOf
import com.ivianuu.essentials.ui.common.setValue
import com.ivianuu.essentials.ui.insets.InsetsPadding
import com.ivianuu.essentials.ui.material.AppBarStyle
import com.ivianuu.essentials.ui.material.LocalAppBarStyle
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.UrlKey
import com.ivianuu.essentials.ui.systembars.systemBarStyle
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

data class WebKey(val title: String, val url: String) : Key<Unit>

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
