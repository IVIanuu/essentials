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
            Surface(color = MaterialTheme.colors.primary, elevation = 8.dp) {
                InsetsPadding(left = false, top = false, right = false) {
                    val backgroundColor = when (LocalAppBarStyle.current) {
                        AppBarStyle.PRIMARY -> MaterialTheme.colors.primary
                        AppBarStyle.SURFACE -> MaterialTheme.colors.surface
                    }
                    BottomAppBar(
                        modifier = Modifier
                            .systemBarStyle(backgroundColor),
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
                                navigator.push(UrlKey(webViewRef!!.url))
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
