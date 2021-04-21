package com.ivianuu.essentials.web.ui

import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.viewinterop.*
import androidx.compose.ui.window.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*

data class WebKey(val title: String, val url: String) : Key<Nothing>

@Given
fun webUi(
    @Given key: WebKey,
    @Given navigator: Navigator
): KeyUi<WebKey> = {
    var webViewRef: WebView? by remember { refOf(null) }
    DisposableEffect(true) {
        onDispose {
            webViewRef?.destroy()
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text(key.title) }) },
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { webViewRef!!.goBack() }) {
                    Icon(painterResource(R.drawable.es_ic_arrow_back_ios_new), null)
                }
                IconButton(onClick = { webViewRef!!.reload() }) {
                    Icon(painterResource(R.drawable.es_ic_refresh), null)
                }
                IconButton(onClick = { navigator.push(UrlKey(webViewRef!!.url)) }) {
                    Icon(painterResource(R.drawable.es_ic_open_in_browser), null)
                }
            }
        }
    ) {
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
