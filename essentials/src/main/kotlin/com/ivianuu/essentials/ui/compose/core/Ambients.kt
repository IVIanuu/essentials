package com.ivianuu.essentials.ui.compose.core

import android.app.Activity
import androidx.compose.Ambient
import com.ivianuu.essentials.ui.base.EsController
import com.ivianuu.essentials.ui.navigation.Route

val ActivityAmbient = Ambient.of<Activity> { error("No activity found") }
val ControllerAmbient = Ambient.of<EsController> { error("No controller found") }
val RouteAmbient = Ambient.of<Route> { error("No route found") }