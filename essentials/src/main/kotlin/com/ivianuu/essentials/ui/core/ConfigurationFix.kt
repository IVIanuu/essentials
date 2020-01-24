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

package com.ivianuu.essentials.ui.core

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.ContextAmbient

// todo remove once ConfigurationAmbient triggers recomposition

@Composable
fun ConfigurationFix(children: @Composable () -> Unit) {
    val application = ContextAmbient.current.applicationContext as Application

    val configuration = state { application.resources.configuration }
    val callbacks = remember {
        object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                configuration.value = Configuration()
                    .apply { updateFrom(application.resources.configuration) }
            }

            override fun onLowMemory() {
            }

            override fun onTrimMemory(level: Int) {
            }
        }
    }

    onActive {
        application.registerComponentCallbacks(callbacks)
        onDispose { application.unregisterComponentCallbacks(callbacks) }
    }

    Providers(
        ConfigurationAmbient provides configuration.value,
        children = children
    )
}
