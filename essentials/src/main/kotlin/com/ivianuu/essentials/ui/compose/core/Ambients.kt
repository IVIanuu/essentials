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

package com.ivianuu.essentials.ui.compose.core

import android.app.Activity
import androidx.compose.Ambient
import androidx.ui.graphics.Color
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.navigation.Route

val ActivityAmbient = Ambient.of<Activity> { error("No activity found") }
val ControllerAmbient = Ambient.of<EsController> { error("No controller found") }
val RouteAmbient = Ambient.of<Route> { error("No route found") }
val CurrentBackground = Ambient.of<Color> { Color.White }