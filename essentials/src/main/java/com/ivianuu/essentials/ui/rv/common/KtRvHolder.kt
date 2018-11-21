package com.ivianuu.essentials.ui.rv.common

import android.view.View
import com.ivianuu.essentials.ui.rv.RvHolder
import kotlinx.android.extensions.LayoutContainer

/**
 * @author Manuel Wrage (IVIanuu)
 */
open class KtRvHolder : RvHolder(), LayoutContainer {
    override lateinit var containerView: View

    override fun bindView(itemView: View) {
        containerView = itemView
    }
}