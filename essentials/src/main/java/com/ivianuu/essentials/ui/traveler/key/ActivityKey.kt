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

abstract class ActivityKey(
    val target: KClass<out Activity>,
    open val startOptions: StartOptions? = null
) : ActivityKey, Parcelable {

    override fun createIntent(context: Context, data: Any?): Intent =
        Intent(context, target.java).also { addTo(it) }

    override fun createStartActivityOptions(command: Command, activityIntent: Intent): Bundle? =
        startOptions?.get(command, activityIntent)

    interface StartOptions {
        fun get(command: Command, activityIntent: Intent): Bundle? = null
    }
}

fun com.ivianuu.essentials.ui.traveler.key.ActivityKey.addTo(intent: Intent) {
    intent.putExtra(TRAVELER_KEY, this)
    intent.putExtra(TRAVELER_KEY_CLASS, javaClass.name)
}

fun <T : Parcelable> Activity.getKey(): T = intent!!.extras!!.getParcelable(TRAVELER_KEY)!!

fun <T : Parcelable> Activity.getKeyOrNull(): T? = try {
    getKey()
} catch (e: Exception) {
    null
}

fun <T : Parcelable> Activity.key(): Lazy<T> = unsafeLazy { getKey<T>() }