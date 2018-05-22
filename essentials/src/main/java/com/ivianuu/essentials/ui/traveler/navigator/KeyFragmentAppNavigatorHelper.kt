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

package com.ivianuu.essentials.ui.traveler.navigator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.ivianuu.essentials.ui.traveler.key.ActivityKey
import com.ivianuu.traveler.commands.Command
import com.ivianuu.traveler.commands.Forward
import com.ivianuu.traveler.commands.Replace

/**
 * Helper for key fragment app navigators
 */
class KeyFragmentAppNavigatorHelper {

    private val keyFragmentNavigatorHelper = KeyFragmentNavigatorHelper()

    fun createFragment(key: Any, data: Any?): Fragment? {
        return keyFragmentNavigatorHelper.createFragment(key, data)
    }

    fun setupFragmentTransaction(
        command: Command,
        currentFragment: Fragment?,
        nextFragment: Fragment,
        transaction: FragmentTransaction
    ) {
        keyFragmentNavigatorHelper.setupFragmentTransaction(command,
            currentFragment, nextFragment, transaction)
    }

    fun createActivityIntent(context: Context, key: Any, data: Any?): Intent? {
        return if (key is ActivityKey) {
            key.newIntent(context, data)
        } else {
            null
        }
    }

    fun createStartActivityOptions(command: Command, activityIntent: Intent): Bundle? {
        val key = when (command) {
            is Forward -> command.key as ActivityKey
            is Replace -> command.key as ActivityKey
            else -> null
        }

        return key?.createStartActivityOptions(command, activityIntent)
    }

}