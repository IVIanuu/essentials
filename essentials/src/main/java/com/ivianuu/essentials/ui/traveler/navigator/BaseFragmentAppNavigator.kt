package com.ivianuu.essentials.ui.traveler.navigator

import android.app.Activity
import android.support.v4.app.FragmentManager
import com.ivianuu.compass.CompassFragmentAppNavigator
import com.ivianuu.essentials.ui.traveler.command.Toast
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.traveler.commands.Command

/**
 * Essentials fragment app navigator
 */
open class BaseFragmentAppNavigator(
    private val activity: Activity,
    fragmentManager: FragmentManager,
    containerId: Int
) : CompassFragmentAppNavigator(activity, fragmentManager, containerId) {

    override fun applyCommand(command: Command) {
        when (command) {
            is Toast -> {
                val message = when {
                    command.message != null -> command.message
                    command.messageRes != 0 -> activity.string(command.messageRes, *command.args)
                    else -> throw IllegalArgumentException("must specify one of message or messageRes")
                }
                when (command.type) {
                    Toast.TYPE_ERROR -> activity.toastError(message)
                    Toast.TYPE_INFO -> activity.toastInfo(message)
                    Toast.TYPE_NORMAL -> activity.toastNormal(message)
                    Toast.TYPE_SUCCESS -> activity.toastSuccess(message)
                    Toast.TYPE_WARNING -> activity.toastWarning(message)
                    else -> throw IllegalArgumentException("unknown type -> ${command.type}")
                }
            }
            else -> super.applyCommand(command)
        }
    }

}