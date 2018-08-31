package com.ivianuu.essentials.ui.traveler.command

import com.ivianuu.traveler.commands.Command

data class SnackbarMessage(
    val message: CharSequence?,
    val messageRes: Int,
    val messageArgs: Array<out Any>,

    val actionText: CharSequence?,
    val actionTextRes: Int,
    val actionTextArgs: Array<out Any>
) : Command