package com.ivianuu.essentials.ui.compose

import android.view.View
import androidx.core.view.updateLayoutParams
import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.View
import com.ivianuu.compose.init

fun ComponentComposition.Space(width: Int = 0, height: Int = 0) {
    View<View> {
        init {
            updateLayoutParams {
                this.width = width
                this.height = height
            }
        }
    }
}