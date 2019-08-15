package com.ivianuu.essentials.ui.compose

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.ViewById
import com.ivianuu.compose.ViewByLayoutRes
import com.ivianuu.essentials.R

fun ComponentComposition.Scaffold(
    appBar: (ComponentComposition.() -> Unit)? = null,
    content: (ComponentComposition.() -> Unit)? = null,
    floatingActionButton: (ComponentComposition.() -> Unit)? = null
) {
    ViewByLayoutRes<LinearLayout>(layoutRes = R.layout.es_scaffold) {
        ViewById<FrameLayout>(id = R.id.es_app_bar) { appBar?.invoke(this@Scaffold) }
        ViewById<FrameLayout>(id = R.id.es_content) { content?.invoke(this@Scaffold) }
        floatingActionButton?.invoke(this@Scaffold)
    }
}