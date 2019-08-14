package com.ivianuu.essentials.ui.compose

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.byId
import com.ivianuu.compose.layoutRes
import com.ivianuu.essentials.R

fun ComponentComposition.Scaffold(
    appBar: (ComponentComposition.() -> Unit)? = null,
    content: (ComponentComposition.() -> Unit)? = null,
    floatingActionButton: (ComponentComposition.() -> Unit)? = null
) {
    View<LinearLayout> {
        layoutRes(R.layout.es_scaffold)
        View<FrameLayout> {
            byId(R.id.es_app_bar)
            appBar?.invoke(this@Scaffold)
        }
        View<FrameLayout> {
            byId(R.id.es_content)
            content?.invoke(this@Scaffold)
        }

        floatingActionButton?.invoke(this@Scaffold)
    }
}