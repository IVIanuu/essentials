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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.ivianuu.traveler.commands.Command

/**
 * Key for [Activity]'s
 */
abstract class ActivityKey {

    fun newIntent(context: Context): Intent = createIntent(context).apply {
        if (this@ActivityKey is Parcelable) {
            putExtra(KEY_KEY, this@ActivityKey)
        }
    }

    protected abstract fun createIntent(context: Context): Intent

    open fun createStartActivityOptions(command: Command, activityIntent: Intent): Bundle? {
        return null
    }

    companion object {
        private const val KEY_KEY = "key"

        fun <T> get(activity: Activity) :T? where T : ActivityKey, T : Parcelable =
            activity.intent.getParcelableExtra(KEY_KEY)
    }
}

fun <T> Activity.key() where T : ActivityKey, T : Parcelable =
    ActivityKey.get<T>(this)

fun <T> Activity.requireKey() where T : ActivityKey, T : Parcelable =
    key<T>() ?: throw IllegalStateException("missing key")