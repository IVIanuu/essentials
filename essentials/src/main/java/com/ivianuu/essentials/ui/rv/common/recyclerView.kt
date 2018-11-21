package com.ivianuu.essentials.ui.rv.common

import androidx.recyclerview.widget.RecyclerView
import com.ivianuu.essentials.ui.rv.RvController

fun RecyclerView.setRvController(controller: RvController) {
    adapter = controller.adapter
}