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

import android.content.res.Configuration
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.ConfigurationAmbient
import com.ivianuu.essentials.composehelpers.ambientOf
import com.ivianuu.essentials.composehelpers.current

enum class Orientation {
    Portrait, Landscape
}

@Composable
fun OrientationProvider(children: @Composable() () -> Unit) {
    val configuration = ConfigurationAmbient.current
    val orientation = remember(configuration.orientation) {
        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> Orientation.Portrait
            Configuration.ORIENTATION_LANDSCAPE -> Orientation.Landscape
            else -> error("Unexpected orientation ${configuration.orientation}")
        }
    }

    OrientationAmbient.Provider(value = orientation, children = children)
}

val OrientationAmbient =
    ambientOf<Orientation> { error("No orientation provided") }
