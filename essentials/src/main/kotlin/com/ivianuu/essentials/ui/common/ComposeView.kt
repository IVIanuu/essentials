package com.ivianuu.essentials.ui.common

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.ui.core.setContent

fun ComposeView(
    context: Context,
    content: @Composable () -> Unit
): View = FrameLayout(context).also { container ->
    container.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        private var composition: Composition? = null
        override fun onViewAttachedToWindow(v: View?) {
            composition = container.setContent(content)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            composition?.dispose()
            composition = null
        }
    })
}
