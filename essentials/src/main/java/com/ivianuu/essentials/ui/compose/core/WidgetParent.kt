package com.ivianuu.essentials.ui.compose.core

interface WidgetParent {
    fun insertChild(index: Int, child: Widget)
    fun moveChild(from: Int, to: Int, count: Int)
    fun removeChild(index: Int, count: Int)
    fun updateChild(child: Widget)
}