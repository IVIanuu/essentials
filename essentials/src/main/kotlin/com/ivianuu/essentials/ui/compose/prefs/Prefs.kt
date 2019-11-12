/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.ui.compose.prefs

import android.content.SharedPreferences
import androidx.compose.Composable
import androidx.compose.unaryPlus
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.coroutines.collect
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.android.ApplicationScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import java.util.*

@Composable
fun Prefs(children: () -> Unit) = composable("Prefs") {
    val sharedPreferencesChanges = +inject<SharedPreferencesChanges>()
    +collect(sharedPreferencesChanges.changes.map { UUID.randomUUID().toString() })
    children()
}

@Inject
@ApplicationScope
class SharedPreferencesChanges(sharedPreferences: SharedPreferences) :
    SharedPreferences.OnSharedPreferenceChangeListener {

    val changes: Flow<String>
        get() = channel.asFlow()

    private val channel = BroadcastChannel<String>(1)

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        channel.offer(key)
    }
}