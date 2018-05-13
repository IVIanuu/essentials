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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.ivianuu.conductor.Controller
import com.ivianuu.conductor.Router
import com.ivianuu.conductor.RouterTransaction
import com.ivianuu.traveler.commands.Command
import com.ivianuu.traveler.commands.Forward
import com.ivianuu.traveler.commands.Replace
import com.ivianuu.traveler.conductorfork.ControllerAppNavigator

/**
 * Navigator for key based navigation
 */
open class ControllerKeyNavigator(
    activity: FragmentActivity,
    router: Router
): ControllerAppNavigator(activity, router) {

    override fun createActivityIntent(context: Context, key: Any, data: Any?): Intent? {
        return if (key is ActivityKey) {
            key.newIntent(context, data)
        } else {
            null
        }
    }

    override fun createStartActivityOptions(command: Command, activityIntent: Intent): Bundle? {
        val key = when(command) {
            is Forward -> command.key as ActivityKey
            is Replace -> command.key as ActivityKey
            else -> null
        }

        return key?.createStartActivityOptions(command, activityIntent)
    }

    override fun createController(key: Any, data: Any?): Controller? {
        return if (key is ControllerKey) {
            key.newInstance(data)
        } else {
            null
        }
    }

    override fun setupRouterTransaction(command: Command, transaction: RouterTransaction) {
        val key = when(command) {
            is Forward -> command.key as ControllerKey
            is Replace -> command.key as ControllerKey
            else -> null
        }

        key?.setupTransaction(command, transaction)
    }
}