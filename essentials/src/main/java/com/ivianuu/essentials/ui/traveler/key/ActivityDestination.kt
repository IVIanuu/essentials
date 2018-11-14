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

package com.ivianuu.essentials.ui.traveler.key

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.traveler.Command
import com.ivianuu.traveler.android.ActivityKey
import kotlin.reflect.KClass

interface ActivityDestination : ActivityKey, Parcelable {
    val target: KClass<out Activity>

    val activityStartOptionsProvider: ActivityStartOptionsProvider? get() = null

    override fun createIntent(context: Context, data: Any?) =
        Intent(context, target.java).apply {
            putExtra(KEY_DESTINATION, this@ActivityDestination)
        }

    override fun createStartActivityOptions(command: Command, activityIntent: Intent) =
        activityStartOptionsProvider?.createActivityStartOptions(command, activityIntent)
}

abstract class BaseActivityDestination(
    override val target: KClass<out Activity>,
    override val activityStartOptionsProvider: ActivityStartOptionsProvider? = null
) : ActivityDestination

interface ActivityStartOptionsProvider {
    fun createActivityStartOptions(command: Command, activityIntent: Intent): Bundle? = null
}

fun <T : Parcelable> Activity.destination(): T = intent!!.extras!!.getParcelable(KEY_DESTINATION)!!

fun <T : Parcelable> Activity.destinationOrNull() = try {
    destination<T>()
} catch (e: Exception) {
    null
}

fun <T : Parcelable> Activity.bindDestination() = unsafeLazy { destination<T>() }