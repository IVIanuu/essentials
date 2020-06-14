package com.ivianuu.essentials.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.compose.Recomposer
import androidx.ui.core.setContent

fun ViewGroup.setContentWhileAttached(content: @Composable () -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        private var composition: Composition? = null
        override fun onViewAttachedToWindow(v: View?) {
            composition = setContent(Recomposer.current(), content)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            composition?.dispose()
            composition = null
        }
    })
}
