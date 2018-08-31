package com.ivianuu.essentials.ui.traveler.command

import com.ivianuu.traveler.commands.Command

/**
 * Toast command
 */
data class Toast(
    val type: Int,
    val message: CharSequence?,
    val messageRes: Int,
    val args: Array<out Any>
) : Command {
    companion object {
        const val TYPE_ERROR = 0
        const val TYPE_INFO = 1
        const val TYPE_NORMAL = 2
        const val TYPE_SUCCESS = 3
        const val TYPE_WARNING = 4
    }
}