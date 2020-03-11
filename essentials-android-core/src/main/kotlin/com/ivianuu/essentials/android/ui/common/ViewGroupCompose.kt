package com.ivianuu.essentials.android.ui.common

import android.view.View
import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Composition
import androidx.ui.core.setContent

fun ViewGroup.compose(content: @Composable() () -> Unit) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        private var composition: Composition? = null
        override fun onViewAttachedToWindow(v: View?) {
            composition = setContent(content)
        }

        override fun onViewDetachedFromWindow(v: View?) {
            composition?.dispose()
            composition = null
        }
    })
}
