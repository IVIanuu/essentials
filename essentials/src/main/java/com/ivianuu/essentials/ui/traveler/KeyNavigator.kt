/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.traveler

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.widget.Toast
import com.ivianuu.essentials.ui.traveler.key.ActivityKey
import com.ivianuu.essentials.ui.traveler.key.DialogFragmentKey
import com.ivianuu.essentials.ui.traveler.key.DialogKey
import com.ivianuu.essentials.ui.traveler.key.FragmentKey
import com.ivianuu.traveler.Navigator
import com.ivianuu.traveler.commands.*
import java.util.*

/**
 * Navigator for key based navigation
 */
open class KeyNavigator(
    private val activity: FragmentActivity,
    private val fragmentManager: FragmentManager,
    private val containerId: Int
): Navigator {

    private val handler = Handler()
    private val localStackCopy = LinkedList<String>()

    override fun applyCommands(commands: Array<Command>) {
        handler.post {
            fragmentManager.executePendingTransactions()

            handler.post {
                //copy stack before apply commands
                copyStackToLocal()

                for (command in commands) {
                    applyCommand(command)
                }
            }
        }
    }

    protected open fun applyCommand(command: Command) {
        when (command) {
            is Forward -> forward(command)
            is Back -> back()
            is Replace -> replace(command)
            is BackTo -> backTo(command)
            is SystemMessage -> showSystemMessage(command.message)
        }
    }

    protected open fun forward(command: Forward) {
        when(command.key) {
            is ActivityKey -> forwardActivity(command, command.key as ActivityKey)
            is DialogKey -> forwardDialog(command, command.key as DialogKey)
            is DialogFragmentKey -> forwardDialogFragment(command, command.key as DialogFragmentKey)
            is FragmentKey -> forwardFragment(command, command.key as FragmentKey)
            else -> unknownScreen(command)
        }
    }

    protected open fun forwardActivity(command: Forward,
                                       key: ActivityKey
    ) {
        val activityIntent = key.newIntent(activity)
        val options = key.createStartActivityOptions(command, activityIntent)
        checkAndStartActivity(command.key, activityIntent, options)
    }

    protected open fun forwardDialog(command: Forward,
                                     key: DialogKey
    ) {
        val dialog = key.createDialog(activity)
        dialog.show()
    }

    protected open fun forwardDialogFragment(command: Forward,
                                             key: DialogFragmentKey
    ) {
        val dialogFragment = key.newInstance()
        val tag = key.fragmentTag
        dialogFragment.show(fragmentManager, tag)
    }

    protected open fun forwardFragment(command: Forward,
                                       key: FragmentKey
    ) {
        val fragment = key.newInstance()
        val tag = key.fragmentTag

        val fragmentTransaction = fragmentManager.beginTransaction()

        key.setupFragmentTransactionAnimation(
            command,
            fragmentManager.findFragmentById(containerId),
            fragment,
            fragmentTransaction
        )

        fragmentTransaction
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()

        localStackCopy.add(tag)
    }

    protected open fun back() {
        if (localStackCopy.size > 0) {
            fragmentManager.popBackStack()
            localStackCopy.pop()
        } else {
            exit()
        }
    }

    protected open fun replace(command: Replace) {
        when(command.key) {
            is ActivityKey -> replaceActivity(command, command.key as ActivityKey)
            is FragmentKey -> replaceFragment(command, command.key as FragmentKey)
            else -> unknownScreen(command)
        }
    }

    protected open fun replaceActivity(command: Replace,
                                       key: ActivityKey
    ) {
        val activityIntent = key.newIntent(activity)

        val options = key.createStartActivityOptions(command, activityIntent)
        checkAndStartActivity(command.key, activityIntent, options)
        activity.finish()
    }

    protected open fun replaceFragment(command: Replace,
                                       key: FragmentKey
    ) {
        val fragment = key.newInstance()
        val tag = key.fragmentTag

        if (localStackCopy.size > 0) {
            fragmentManager.popBackStack()
            localStackCopy.pop()

            val fragmentTransaction = fragmentManager.beginTransaction()

            key.setupFragmentTransactionAnimation(
                command,
                fragmentManager.findFragmentById(containerId),
                fragment,
                fragmentTransaction
            )

            fragmentTransaction
                .replace(containerId, fragment, tag)
                .addToBackStack(tag)
                .commit()
            localStackCopy.add(tag)
        } else {
            val fragmentTransaction = fragmentManager.beginTransaction()

            key.setupFragmentTransactionAnimation(
                command,
                fragmentManager.findFragmentById(containerId),
                fragment,
                fragmentTransaction
            )

            fragmentTransaction
                .replace(containerId, fragment, tag)
                .commit()
        }
    }

    protected open fun backTo(command: BackTo) {
        val key = command.key

        if (key == null
            || key !is FragmentKey) {
            backToRoot()
        } else {
            val index = localStackCopy.indexOf(key.fragmentTag)
            val size = localStackCopy.size

            if (index != -1) {
                for (i in 1 until size - index) {
                    localStackCopy.pop()
                }
                fragmentManager.popBackStack(key.fragmentTag, 0)
            } else {
                backToUnexisting(command)
            }
        }
    }

    protected open fun backToUnexisting(key: Any) {
        backToRoot()
    }

    protected open fun unknownScreen(command: Command) {
        throw RuntimeException("Can't create a screen for passed screenKey.")
    }

    protected open fun unexistingActivity(key: Any, intent: Intent) {
        // Do nothing by default
    }

    protected open fun showSystemMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    protected open fun exit() {
        activity.finish()
    }

    private fun copyStackToLocal() {
        localStackCopy.clear()

        val stackSize = fragmentManager.backStackEntryCount
        (0 until stackSize).mapTo(localStackCopy) { fragmentManager.getBackStackEntryAt(it).name }
    }

    private fun backToRoot() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        localStackCopy.clear()
    }

    private fun checkAndStartActivity(key: Any, activityIntent: Intent, options: Bundle?) {
        // Check if we can start activity
        if (activityIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(activityIntent, options)
        } else {
            unexistingActivity(key, activityIntent)
        }
    }
}